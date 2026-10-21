# Google Docs — System Design

> Source: Hello Interview, "Google Docs" by Stefan Mai (Jul 14, 2024) · Difficulty: Hard
> Asked at: Amazon, Google, Fountain, +6
> Pattern: Real-time Updates

---

## Table of Contents

1. [Understanding the Problem](#understanding-the-problem)
2. [Functional Requirements](#functional-requirements)
3. [Non-Functional Requirements](#non-functional-requirements)
4. [Set Up](#set-up)
   - [Planning the Approach](#planning-the-approach)
   - [Core Entities](#core-entities)
   - [API](#api)
5. [High-Level Design](#high-level-design)
   - [1. Create new documents](#1-users-should-be-able-to-create-new-documents)
   - [2. Concurrent editing](#2-multiple-users-should-be-able-to-edit-the-same-document-concurrently)
   - [3. Real-time updates](#3-users-should-be-able-to-view-each-others-changes-in-real-time)
   - [4. Cursor position and presence](#4-users-should-be-able-to-see-the-cursor-position-and-presence-of-other-users)
6. [Deep Dives](#deep-dives)
   - [1. Scaling to millions of websocket connections](#1-how-do-we-scale-to-millions-of-websocket-connections)
   - [2. Keeping storage under control](#2-how-do-we-keep-storage-under-control)
7. [Final Design](#final-design)
8. [Additional Deep Dives](#additional-deep-dives-to-consider)
9. [What Is Expected at Each Level](#what-is-expected-at-each-level)
10. [References](#references)
11. [Knowledge Check](#knowledge-check)
12. [Notable Community Discussion](#notable-community-discussion)

---

## Understanding the Problem

### What is Google Docs?

Google Docs is a **browser-based collaborative document editor**. Users can create rich text documents and collaborate with others in real-time.

We'll design a system that supports the core functionality of Google Docs, dipping into **websockets** and **collaborative editing systems** (OT vs. CRDT).

---

## Functional Requirements

### Core Requirements

1. Users should be able to **create new documents**.
2. **Multiple users** should be able to edit the same document **concurrently**.
3. Users should be able to **view each other's changes in real-time**.
4. Users should be able to see the **cursor position and presence** of other users.

### Below the Line (Out of Scope)

- Sophisticated document structure — assume a simple text editor
- Permissions and collaboration levels (who has access to a document)
- Document history and versioning

---

## Non-Functional Requirements

### Core Requirements

1. Documents should be **eventually consistent** — all users eventually see the same document state.
2. Updates should be **low latency (< 100ms)**.
3. Scale to **millions of concurrent users** across **billions of documents**.
4. **No more than 100 concurrent editors per document.**
5. Documents should be **durable and available** even if the server restarts.

> **Helpful constraint:** Limiting concurrent editors per document means we can avoid worrying about massive throughput on a single document and focus on core functionality. Google Docs made this choice too — beyond a certain number of concurrent users, newcomers can only join as readers (which hints at their architecture).

### Whiteboard Summary

```
Functional
* Users can create new documents
* Users can edit documents
* Users can get real-time updates
* Users can see each others' cursors

Non-Functional
* Documents are eventually consistent
* Updates are low latency (100ms)
* Millions of users, billions of documents
* No more than 100 concurrent editors
* Durable documents
```

---

## Set Up

### Planning the Approach

Start by designing a system that satisfies the functional requirements without much concern for scale. Then, in the deep dives, bring back the non-functional concerns one by one.

Steps: enumerate the "nouns" → build an API → draw boxes.

### Core Entities

| Entity | Description |
|---|---|
| **Editor** | A user editing a document. |
| **Document** | A collection of text managed by an editor. |
| **Edit** | A change made to the document by an editor. |
| **Cursor** | The position of the editor's cursor in the document (and the presence of that user). |

```
Editor
Document
Edit
Cursor
```

### API

We need REST APIs to manage the document itself, plus **bi-directional** communication between editors and the document — a **websocket**-based approach for the editing experience.

```
POST /docs
{
  title: string
} -> {
  docId
}

WS /docs/{docId}
  SEND {
    type: "insert"
    ....
  }

  SEND {
    type: "updateCursor"
    position: ...
  }

  SEND {
    type: "delete"
    ...
  }

  RECV {
    type: "update"
    ...
  }
```

> With websockets you're describing **messages sent over the socket** vs. **HTTP endpoints**. Notation is up to you, but have some way to describe the protocol/message schema.

> **Tactic:** Tell your interviewer there are more API details to work through, and you'll return to them as you flesh out the problem. Acknowledging issues and leaving room to adjust keeps the interview moving.

---

## High-Level Design

### 1) Users should be able to create new documents

A simple API that takes a title and returns a document ID. There's interesting metadata to hang off a document (permissions, history, tags), so assume separation between the **document itself** and its **metadata**.

```
User ──► API Gateway ──► Document Metadata Service ──► Document Metadata DB (Postgres)
              POST /docs                                  Document { id, title }
```

- **Database:** Postgres — flexible querying; partition and replicate later if needed.
- A horizontally scaled CRUD service fronted by an API gateway is so common you should sling it out quickly. Don't spend much time here.

### 2) Multiple users should be able to edit the same document concurrently

Writing to the document itself has both **consistency** and **scaling** problems. Multiple users making high-frequency edits to the same document is a recipe for contention. Defer scaling to the deep dives; focus on consistency now.

#### ❌ Sending Snapshots (Wrong)

Each edit sends the **entire document** to a Document Service, which stores it in blob storage (S3).

```
User ──WS──► Document Service ──► Blob Store (S3)   [write entire document]
```

Problems:

1. **Inefficient** — transferring the whole document on every edit; 100s of KBs per keystroke for a fast typer.
2. **Lost concurrent edits** — last writer wins.

Example, starting from `Hello!`:

- User A adds `, world` → `Hello, world!`
- User B deletes the `!` → `Hello`
- Both submit simultaneously. Final document depends entirely on which request arrives last. Terrible UX — concurrent edits are lost.

#### ⚠️ Sending Edits (Getting Warmer)

Transmit just the **edits**:

- User A: `INSERT(5, ", world")`
- User B: `DELETE(6)`

Solves bandwidth. But **edits are contextual** — each is based on a specific state of the document:

- If A's edit arrives after B's deletion → `Hello, world` ✅
- If B's edit arrives after A's edit → `Hello world!` ❌ (deleted the comma instead of the `!`)

#### Collaborative Editing Approaches

Two options to handle a stream of contextual edits:

##### Option A: Operational Transformation (OT) ✅ (chosen)

**Reinterpret / transform** each edit before it's applied, based on the edits that came before it. Collect all edits on a **single server**; transform in batches.

Worked example: A's `INSERT(5, ", world")` arrives first. B's `DELETE(6)` is transformed to `DELETE(13)` so it deletes the `!` instead of the comma.

```
Initial text: Hello!

Without OT                              With OT
User A -> INSERT(5, ", world")          User A -> INSERT(5, ", world")
User B <- DELETE(6)                     User B <- DELETE(13)  (transformed)
Result: Hello world!  ❌                Result: Hello, world  ✅

User A -> DELETE(6)                     User A -> DELETE(6)
User B <- INSERT(5, ", world")          User B <- INSERT(5, ", world")
Result: Hello, world  ✅                Result: Hello, world  ✅
```

**Pros:** Low memory, fast, well-suited to text.

**Challenges:** Requires a **central server** to provide a final ordering of operations — scales to a small number of collaborators, not an enormous number (our 100-editor cap helps here). Tricky to implement correctly and easy to get wrong.

##### Option B: Conflict-free Replicated Data Types (CRDTs)

Make every edit **commutative** — applicable in any order with the same result. Two tricks for text:

1. **Infinitely subdividable positions.** Positions are unique identifiers (conceptually real numbers; implementations use variable-length IDs) that never change on insertion. Need more "space"? Generate a new ID between any two existing ones.
2. **Tombstones** for deleted text. Text is never removed; we remember it's deleted before display.

Worked example — both users insert after "Hello":

```
H  e  l  l  o  !
0  1  2  3  4  5

User A inserts "," at 4.3:
H    e    l    l    o    ,    !
0    1    2    3    4    4.3  5

User B (simultaneously) inserts " there" at 4.1..4.8:
H    e    l    l    o         t    h    e    r    e    !
0    1    2    3    4    4.1  4.2  4.4  4.5  4.7  4.8  5

Merged (total ordering by position):
H    e    l    l    o         t    ,    h    e    r    e    !
0    1    2    3    4    4.1  4.2  4.3  4.4  4.5  4.7  4.8  5
```

Result: `Hello t,here!` — a consequence of both editing the same position. Inelegant, but the core guarantee holds: **every client converges on the same document regardless of arrival order**.

**Pros:** No central server needed — good for peer-to-peer (e.g., WebRTC) and **offline** use cases (buffer edits locally, sync later). Great open-source implementation: **Yjs**.

**Challenges:** Higher memory — remember every edit including tombstones, so documents only grow. Less computationally efficient (workarounds exist). Conflict handling can be inelegant, especially when clients buffer or go offline.

##### Decision

**Use OT.** It's what Google Docs does; it needs low memory, adapts well to text, and we have a centralized server anyway. If we needed many more collaborators or peer-to-peer, we'd use CRDTs (Figma is a notable industrial CRDT example).

#### Updated design

Send edits to a central server as **operations**, which are **transformed** by the server before being recorded.

```
User ──WS──► Document Service ──► Document Operations DB (Cassandra)
            (Transform Ops)        Operations { timestamp, documentId, data }
            [write just edits]
```

- **Document Operations DB:** Cassandra — fast append-only writes. Partition by `documentId`, order by `timestamp` (set by the Document Service).
- Once the edit is recorded, ack it to the user. **Durability requirement satisfied.**

### 3) Users should be able to view each other's changes in real-time

Two read paths:

#### When the document is loaded

When a websocket connection is first established, push **all previous applied operations** to the connection. Since everyone connects to the same server, all connections start from the same place.

```
On connect: Document Service reads all ops from Operations DB ──► sends all applied ops to client
```

#### When updates happen

After recording an operation, the Document Service **broadcasts it to all clients connected to the same document**.

**Clients must also perform OT.** Users expect to see their own edits immediately — local changes are applied first, then shipped to the server. If another user's edit lands on the server after our local change but before ours is recorded, the client must transform.

If User A submits `Ea` and User B submits `Eb` (arriving after `Ea`), the perceived order at each site is:

```
Server: Ea, Eb
User A: Ea, Eb
User B: Eb, Ea
```

Regardless of order, applying OT guarantees each user sees the same document.

```
User ◄──WS──► Document Service ◄──► Document Operations DB
 (transform     (transform ops,
  received ops)  broadcast to all
                 connected clients)
```

### 4) Users should be able to see the cursor position and presence of other users

"Awareness" avoids simultaneous editing of the same content. Cursor data is **transient and ephemeral with the connection** — we don't care where a cursor was an hour ago or where a disconnected user's cursor is.

So **don't store it in the document data.** Instead:

- Users report cursor changes to the Document Service, which stores them **in memory** and broadcasts via the same websocket.
- On new connection, the Document Service sends other users' cursor/presence from memory.
- On socket hangup/disconnect, remove the user and broadcast the departure.

With that, we have a functional collaborative editor that scales to a few thousand users.

---

## Deep Dives

### 1) How do we scale to millions of websocket connections?

Our weakest assumption: a **single Document Service server**. This won't scale to millions of concurrent users and is a single point of failure.

When a client connects, it either (a) needs to be **co-located** with all other users on the same document, or (b) know where all the other users are connected.

```
User A ──┐
User B ──┼──► Server 1          "I need to send a message to User C —
                                 which server do I connect to?"
User C ──────► Server 2
```

> **Pattern — Real-time Updates:** Push updates to clients across a scaled backend. The jump many candidates fail to make is the **right-to-left arrows**: how do internal services talk back to clients?

> **Tip:** Handle websockets at the **"edge"** of your design. Terminate them early and expose an internal API so the rest of the system stays stateless.

#### Solution: Consistent hash ring + ZooKeeper

Horizontally scale the Document Service and use a **consistent hash ring** to distribute connections. Each server owns a range of hash values, so we always know which server is responsible for a document. **Apache ZooKeeper** maintains ring configuration and coordinates servers.

**Connection flow:**

1. Client opens an HTTP connection to any document server (e.g., round robin) with the requested document ID.
2. That server checks the hash ring for the document ID. If it's not responsible, it **redirects** to the correct server. Clients eventually connect directly to the right server.
3. The HTTP connection is **upgraded to a websocket**. The server maintains a map from document ID → websockets.
4. Server loads stored ops (if not already loaded) from the Document Operations DB.
5. Since all users of a document are on the same server, updates are simply passed to all connected clients.

**Why consistent hashing:** adding/removing servers redistributes only a small portion of connections. Servers periodically check ZooKeeper for ring changes and initiate connection transfers.

```
User ──► API Gateway ──► Document Metadata Service ──► Document Metadata DB
   │
   └──WS──► Document Service (N instances) ──► Document Operations DB
                 │   (transform ops, broadcast,
                 │    in-memory cursors)
                 └──► ZooKeeper (query hash ranges)
```

**Downside:** Scaling events move a lot of state — displaced websocket connections (disconnect + reconnect to the right server) **and** document operations.

**Follow-up topics an interviewer may probe:**

| Area | Concerns |
|---|---|
| Scaling complexity | Track both old and new hash rings when adding servers; requests may go to multiple servers during transitions; server failures need quick redistribution and ring updates |
| Connection state management | Robust monitoring for server failures; client-side reconnection logic |
| Capacity planning | Ensure servers have resources for their connection load; monitor distribution to prevent hotspots |

### 2) How do we keep storage under control?

With billions of documents: if each is 50 KB, that's **50 TB**. Worse, a document with millions of operations requires every op to be transferred and applied to every joining client. And all active documents must be held in **memory** on the Document Service.

**Solution: periodically snapshot / compact operations** — collapse many ops into one (probably a single insert).

#### Approach A: Separate Compaction Service

A Compaction Service periodically reads ops from the Operations DB and writes a compacted snapshot back. Targets: large documents, not recently compacted, not recently written.

**Atomicity problem:** Cassandra only offers row-level transactions. Compaction must not touch a live document.

**Fix — `documentVersionId`:** Add it to the Document Metadata DB. Before loading, read `documentVersionId` to know which ops to retrieve. To change operations, write new ops under a new version and **flip** the `documentVersionId`. All flips go through the Document Service to avoid races.

Compaction Service steps:

1. Read the document's ops from the DB.
2. Compact into as few ops as possible (likely a single insert).
3. Write the result under a **new `documentVersionId`**.
4. Tell the Document Service to flip old → new version.
5. If the Document Service has the document **loaded**, it **discards the flip** to defer compaction (avoids corrupting live ops).

```
Document Metadata DB: Document { id, title, documentVersionId }
Operations DB:        Operations { timestamp, documentVersionId, metadata }

Compaction/Cleanup Service ──► reads/writes Operations DB
                           ──► "Flip Document Version" ──► Document Service
```

**Challenges:** Orchestration hides hairy bugs. Even offline, compaction is a major source of reads/writes — **throttle carefully**.

#### Approach B: Document Service compacts on idle ✅

The Document Service has **exclusive ownership** of a document (when not scaling), so it can compact safely without coordination. Natural trigger: the **last client disconnects** — the service knows immediately and already has all ops in memory.

When the last client disconnects:

1. Offload existing ops to a separate **low-CPU-priority (nice) process** for compaction.
2. Write the resulting ops to the DB under a new `documentVersionId`.
3. Flip `documentVersionId` in the Document Metadata DB.

**Challenges:** Risk of increasing operation latency (especially **P99**). Run compaction in a separate process with lower CPU priority.

---

## Final Design

```
                     POST /docs
User ──► API Gateway ──────────► Document Metadata Service ──► Document Metadata DB (Postgres)
  │                                                            Document { id, title, documentVersionId }
  │
  │  Websocket
  │  (write/receive edits, cursor changes)
  │
  └──────────────► Document Service (horizontally scaled, consistent hash ring)
                     │   - Transform ops (OT)
                     │   - Broadcast applied ops to connected clients
                     │   - In-memory cursor/presence
                     │   - Compact on idle (low-priority process), flip documentVersionId
                     │
                     ├──► ZooKeeper  (query hash ranges, ring membership)
                     │
                     └──► Document Operations DB (Cassandra)
                            Operations { timestamp, documentVersionId, metadata }
                            - On initialization: read all ops → send to client on connect
                            - Append each transformed op

Client: applies local edits immediately, transforms received ops (client-side OT)
```

**Key design decisions recap:**

| Concern | Decision |
|---|---|
| Consistency | Operational Transformation on a central server per document; client-side OT too |
| Transport | Websockets for edits/cursors; REST for document CRUD |
| Metadata storage | Postgres |
| Operations storage | Cassandra, partitioned by document, ordered by server timestamp, append-only |
| Durability | Ack edit only after op is persisted |
| Presence/cursors | In-memory on Document Service, ephemeral with the connection |
| Scaling websockets | Consistent hash ring over Document Service instances, coordinated via ZooKeeper; redirect-on-connect |
| Storage control | Compact ops into a snapshot op when document goes idle; `documentVersionId` flip for atomicity |

---

## Additional Deep Dives to Consider

- **Read-Only Mode.** Google Docs lets users view without interfering. It's far more scalable — millions of readers on one document. How might we implement this?
- **Versioning.** Revert to previous versions. How can the snapshot/compact approach be extended?
- **Memory.** Document Service memory can bottleneck. How to optimize further?
- **Offline Mode.** What changes if clients operate offline? (Hint: CRDTs shine here.)

---

## What Is Expected at Each Level?

### Mid-level

The author probably wouldn't ask this of a mid-level candidate. If asked: create a high-level design, then (with assistance) think through issues in a high-concurrency problem. Show you can think on your feet, spitball solutions, and have a small but growing toolbox.

### Senior

Immediately grok the **consistency and durability** challenges. May start basic and inefficient, but proactively identify bottlenecks and solve them. Not expected to know OT vs. CRDT, but should discuss database tradeoffs and limitations. Possibly time for one additional deep dive.

### Staff

Mastery. Loosely familiar with CRDTs, intimately familiar with **scaling socket services, consistency tradeoffs, serialization problems, transactions**. Get through the deep dives and probably tack on an extra one.

---

## References

- Real-Time Mouse Pointers: How Canva enables collaborative editing using WebRTC.
- Wikipedia: Operational Transformation; Conflict-free Replicated Data Types.
- Yjs — open-source CRDT implementation.

---

## Knowledge Check

**Q1.** What is the primary problem with having each editor send a full document snapshot to the server on every edit?

1. It doesn't support rich text formatting
2. **Concurrent edits from different users overwrite each other** ✅
3. The server needs too much CPU
4. It requires persistent websocket connections for every active client

---

## Notable Community Discussion

- **Difficulty:** Many readers felt this should be rated **hard** (it now is).
- **Why not Redis Pub/Sub like FB Live Comments?** OT requires all ops for a document to be present on a **single central server**, so all editors of a doc must connect to the exact same Document Service instance. Pub/sub makes sense for Live Comments (viewers can be on different servers) but adds nothing here. Also, if a client goes offline and intermediate ops in Redis expire (TTL), the server can't perform OT on the stale op — you'd need the DB anyway, so stick with consistent hashing + DB.
- **Why not have the L7 load balancer do hash-ring routing instead of server redirect?** Possible in principle (Traefik/Envoy with k8s service registry), but uncommon for LBs to implement this logic. Caveat: when the ring changes, Envoy would remap document IDs to new pods even with live sessions on the old one — you still need a stateful registry (ZooKeeper or similar) to say "live connections exist for this key, keep routing there."
- **Security of direct server connections:** Websockets should still go through the API Gateway / L7 LB; the client can fetch a server ID (via ZooKeeper) and pass it in a header for routing, keeping backend servers in a protected network.
- **Where is the document actually stored?** In this design the document is the **sequence of operations** in the Operations DB; after compaction it's a single `insert {contents}` op. Interweaving with blob storage (S3) for compacted snapshots — akin to a database WAL + checkpoint (ARIES) — is a good extension: store the snapshot in S3, reference it from metadata, apply ops with timestamp > snapshot on load.
- **CRDT position collisions:** The 4.3 / 4.1–4.8 example uses round numbers for illustration. Real implementations use long random fractions (e.g., 4.3423442346234) plus jitter and a **site-ID tiebreaker**, making collisions extremely unlikely.
- **Why client-side OT?** The client may have local changes in flight while transformed ops arrive from the server. Simply overwriting with the server result would lose local edits, so clients must transform incoming ops against pending local ops (similar to revision-id-based merges in other collaborative editors).
