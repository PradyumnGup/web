# Google Docs — System Design Revision Notes (Amazon SDE-2)

> Source: HelloInterview "Design Google Docs" (Stefan Mai). Difficulty: Hard. Asked at Amazon, Google.
> Core pattern: **Real-time Updates** (bi-directional push over WebSockets) + **Collaborative Editing (OT vs CRDT)**.

---

## 0. 60-Second Summary (say this if you have no time)

- Metadata via a plain CRUD service (API GW → Metadata Service → Postgres).
- Editing over **WebSockets** to a **Document Service**. Clients send *operations* (insert/delete), not snapshots.
- Server does **Operational Transformation (OT)** to resolve concurrent edits, appends ops to **Cassandra** (partition by `docId`, cluster by `timestamp`), then broadcasts to all connected editors. Clients also apply OT locally.
- **Cursor/presence** is ephemeral → kept in server memory, broadcast over the same socket, dropped on disconnect.
- Scale: horizontally scale Document Service with **consistent hashing on `docId`** (coordinated by ZooKeeper) so all editors of one doc land on one server. Redirect on first connect.
- Storage: **periodic snapshot/compaction** of ops so new joiners don't replay millions of ops and memory stays bounded.

---

## 1. Requirements

### Functional (in scope)
1. Create new documents.
2. Multiple users edit the same document concurrently.
3. Users see each other's changes in real-time.
4. Users see each other's cursor position and presence.

### Out of scope (say it out loud)
- Rich document structure (assume plain text).
- Permissions / sharing levels.
- History / versioning (but mention compaction can be extended for this).

### Non-Functional
| Requirement | Why it matters to the design |
|---|---|
| Eventually consistent — everyone converges to the same doc | Drives OT/CRDT choice |
| Low latency updates (< 100 ms) | WebSockets, in-memory state, co-located editors |
| Millions of concurrent users, billions of docs | Horizontal scaling, consistent hashing, compaction |
| **≤ 100 concurrent editors per doc** | Huge simplifier: a single server can own a doc. Google does this too — beyond a limit, new users are read-only |
| Durable even if server restarts | Persist every op before ACK |

> Tip: Call out that the 100-editor cap is a *helpful* constraint. It justifies a centralized-server-per-doc design (OT) instead of fully decentralized (CRDT).

---

## 2. Core Entities
- **Editor** — a user editing a doc.
- **Document** — the text + metadata.
- **Edit / Operation** — a change (insert/delete) made by an editor.
- **Cursor** — editor's position + presence (ephemeral).

---

## 3. API

REST for management, WebSocket for the editing session.

```
POST /docs
  { title } -> { docId }

WS /docs/{docId}
  SEND { type: "insert", position, text, version }
  SEND { type: "delete", position, length, version }
  SEND { type: "updateCursor", position }
  RECV { type: "update", op, version }      // transformed op from another editor
  RECV { type: "cursor", userId, position }
  RECV { type: "presence", userId, joined|left }
```

> With WebSockets you describe *messages over the wire*, not endpoints. Notation is up to you; just make the protocol explicit.

---

## 4. High-Level Design

### 4.1 Create documents
`User → API Gateway → Document Metadata Service → Postgres (id, title, owner, createdAt)`

- Stateless, horizontally scaled CRUD. Don't spend time here.
- Postgres: flexible querying; shard/replicate later if needed.
- Separate **metadata** from **document content** — they have very different access patterns.

### 4.2 Concurrent editing — build up from the wrong answer

**(a) Send whole document snapshots — WRONG**
- Hundreds of KB per keystroke.
- Last-writer-wins: `Hello!` → A writes `Hello, world!`, B writes `Hello` → one edit is lost entirely.

**(b) Send edits (ops) — closer**
- A: `INSERT(5, ", world")`, B: `DELETE(6)`.
- Fixes bandwidth, but ops are *contextual*: B's `DELETE(6)` assumed `!` was at index 6. If A's insert lands first, B deletes the comma → `Hello world!`.

**(c) Resolve concurrency: OT or CRDT** → see Section 5. **We choose OT.**

Resulting flow:
```
User ⇄ WebSocket ⇄ Document Service (transform ops) → Document Operations DB (Cassandra)
```
- **Cassandra** for the ops log: append-only, high write throughput. Partition key `documentId`, clustering key `timestamp` (assigned by server, giving a total order per doc).
- ACK to client **only after** the op is persisted → durability requirement satisfied.

### 4.3 Real-time reads
Two paths:
1. **On connect / load**: server reads all ops (or latest snapshot + ops since) for the doc from DB and pushes them to the new connection. All editors of a doc are on the same server, so they share the same base state.
2. **On update**: after persisting an op, server broadcasts the transformed op to every other socket on that doc.

**Client-side OT is also required.** Clients apply their own edit locally first (optimistic, feels instant), then ship it. If another op lands on the server in between, the client must transform the incoming op against its pending local ops.

```
Server sees:  Ea, Eb
User A sees:  Ea, Eb
User B sees:  Eb, Ea      ← OT guarantees same final doc regardless
```

### 4.4 Cursor & presence
- Transient data — meaningless once the user disconnects. **Don't persist it.**
- Client sends `updateCursor` → Document Service keeps `{docId → {userId → cursor}}` in memory → broadcasts to the other sockets.
- New joiner receives the current in-memory presence map on connect.
- On socket close/hangup, remove the user and broadcast `left`.

At this point: working collaborative editor, scales to a few thousand users on one server.

---

## 5. OT vs CRDT (expect to be asked — know both)

| | Operational Transformation (OT) | CRDT |
|---|---|---|
| Idea | **Transform** each op against ops that came before it so it applies correctly | Make every op **commutative** — apply in any order, same result |
| Needs central server? | **Yes** — server defines the total order | **No** — peer-to-peer friendly |
| Memory | Low | High — tombstones for deletes, unique IDs per char; doc only grows |
| CPU | Fast | Heavier |
| Offline support | Harder | Natural — buffer ops locally, merge later |
| Implementation | Tricky, easy to get wrong | Library support (e.g., **Yjs**) |
| Conflict UX | Clean | Can interleave awkwardly (`Hello t,here!`) |
| Used by | **Google Docs** | Figma (customized), many P2P apps |

### OT worked example
Start `Hello!`. A: `INSERT(5, ", world")` arrives first. B: `DELETE(6)` (meant the `!`).
Server transforms B's op → `DELETE(13)`. Final: `Hello, world` ✔. Without OT: `Hello world!` ✘.

### CRDT worked example
Each char has a fractional position: `H0 e1 l2 l3 o4 !5`.
A inserts `,` at 4.3. B inserts ` there` at 4.1, 4.2, 4.4, 4.5, 4.7, 4.8.
Sort by position → `Hello t,here!` — same on every client regardless of arrival order. Deletes become tombstones (hidden, not removed).

### Why we pick OT here
- ≤100 editors/doc → a single coordinating server per doc is fine.
- Low memory, good for text.
- Google Docs does it.
- If asked "what if thousands of editors or P2P/WebRTC?" → CRDT.

---

## 6. Deep Dives

### 6.1 Scaling to millions of WebSocket connections
Problem: one Document Service = SPOF and won't scale. Once you have many servers, **how does the backend push to a client connected to a different server?** (The "right-to-left arrow" candidates forget.)

Two options: (a) co-locate all editors of a doc on one server, or (b) servers know where each other's users are (pub/sub). With the 100-editor cap, **(a)** is simpler and keeps OT centralized.

**Solution: consistent hash ring on `docId`, config held in ZooKeeper.**
1. Client opens HTTP to any Document Service (round-robin via LB) with `docId`.
2. Server checks ring; if it doesn't own that hash range → **redirect** client to the owner.
3. Owner upgrades HTTP → WebSocket, stores `docId → [sockets]`.
4. Owner loads ops (snapshot + tail) from Cassandra into memory if not already loaded.
5. All edits for the doc hit this server → transform, persist, broadcast locally. No cross-server fan-out needed.
6. Servers watch ZooKeeper for ring changes; on add/remove only a small fraction of docs move (the point of consistent hashing).

Terminating WebSockets at the "edge" keeps the rest of the system stateless.

**Downsides / follow-ups to be ready for:**
- Scaling events move state: displaced clients are disconnected and reconnect to the new owner; in-memory doc state must be reloaded there.
- During transitions, may need to track old + new rings; requests may briefly go to two servers.
- Server failure → fast ring update + client reconnect logic with backoff; ops are durable in Cassandra so nothing is lost.
- Need monitoring for hot servers / uneven distribution; use virtual nodes to smooth the ring.
- Capacity planning: connections per server bounded by memory and file descriptors.

### 6.2 Keeping storage (and memory) under control
- Billions of docs × 50 KB ≈ 50 TB just for content; a doc with millions of ops must be replayed by every new joiner and held in memory.
- **Snapshot / compact periodically**: collapse ops up to version *N* into a materialized document, store it (blob store/S3 or Cassandra), keep only ops after *N*.
- On load: fetch latest snapshot + ops since → far less to replay, far less memory.
- Trigger by op count, time, or when a doc goes idle/evicted from memory.
- Evict idle docs from server memory (LRU) — reload lazily on next connect.
- Snapshots are also the natural hook for **versioning** later.

### 6.3 Extra deep dives to have a sentence on
- **Read-only mode**: millions of viewers per doc. Viewers don't need OT or a seat on the owner server — fan out via pub/sub (Redis/Kafka) to stateless read servers, or poll periodic snapshots via CDN. Only editors hit the hash-ring owner.
- **Versioning**: keep snapshots instead of deleting them; each snapshot = a version (`documentVersionId`). Revert = new ops that transform current state back.
- **Memory**: compaction, evicting idle docs, compact op encodings, cap ops held in memory.
- **Offline mode**: queue ops client-side with base version; on reconnect, server transforms them against everything since. Long offline windows → many conflicts → CRDTs become attractive.

---

## 7. Final Architecture

```
            ┌──────────────┐  POST /docs  ┌──────────────────────┐     ┌────────────┐
 User ──────►  API Gateway ├─────────────►│ Document Metadata Svc├────►│  Postgres  │
            └──────┬───────┘              └──────────────────────┘     └────────────┘
                   │ WS /docs/{docId}  (redirect to ring owner)
                   ▼
            ┌───────────────────────────┐        ┌────────────┐
            │ Document Service (N nodes)│◄──────►│ ZooKeeper  │  hash ring
            │ • owns docs by hash(docId)│        └────────────┘
            │ • in-memory doc + cursors │
            │ • OT transform + broadcast│
            └─────────────┬─────────────┘
                          │ append ops / read snapshot+ops
                          ▼
            ┌───────────────────────────┐
            │ Document Operations DB    │  Cassandra: PK docId, CK timestamp
            │ + periodic snapshots      │  (snapshots → S3 or same store)
            └───────────────────────────┘
```

---

## 8. Technology Choices — one-liners for "why X?"
- **WebSockets**: bi-directional, low latency push; needed for < 100 ms updates and cursor streaming.
- **Postgres (metadata)**: relational, flexible queries, small volume; shard later.
- **Cassandra (ops log)**: append-heavy writes, partition by `docId` gives locality, time-ordered clustering.
- **ZooKeeper**: coordination/config for the hash ring and server membership.
- **Consistent hashing**: co-locate a doc's editors; minimal remapping on scale events.
- **OT over CRDT**: central server already exists, low memory, text-optimized, ≤100 editors.

---

## 9. Likely Interviewer Questions & Crisp Answers
- *Why not just send the whole document?* Bandwidth + last-writer-wins data loss.
- *Why not just send positional edits?* Edits are contextual; concurrent ops shift positions → wrong text deleted.
- *Why does the client also run OT?* Optimistic local apply means the client's view may be ahead of the server; incoming ops must be transformed against pending local ops.
- *How is durability guaranteed?* Op persisted to Cassandra before ACK; on crash, the new owner replays snapshot + ops.
- *What happens when a Document Service node dies?* ZooKeeper detects, ring updates, clients reconnect (with backoff) and get redirected to the new owner, which reloads from DB.
- *How do you keep all users of a doc on one server?* Consistent hashing on `docId` + redirect on connect.
- *Why cap editors at 100?* Keeps per-doc throughput bounded so one server can serialize ops; mirrors Google's behavior.
- *Where is cursor data stored?* In memory on the owner server only — ephemeral, cleared on disconnect.
- *How do you stop the ops log from growing forever?* Periodic snapshot + compaction; load = snapshot + delta.

---

## 10. Level Expectations (what Amazon SDE-2 ≈ "Senior" bar looks like here)
- **Mid-level**: Build the high-level design; with hints, reason about concurrency issues.
- **Senior (SDE-2 target)**: Immediately spot consistency + durability challenges. Start simple, **proactively** identify bottlenecks (single server, ops growth) and fix them. Discuss DB tradeoffs. Don't *need* to know OT vs CRDT by name, but must handle conflicting edits coherently. One solid deep dive (scaling sockets or storage).
- **Staff**: Fluent in OT/CRDT, socket scaling, consistency tradeoffs, transitions on the hash ring; gets through all deep dives.

### Interview-day checklist
- [ ] State out-of-scope items explicitly.
- [ ] Note the 100-editor cap as a design enabler.
- [ ] Draw CRUD metadata path fast; spend time on the edit path.
- [ ] Walk snapshot → ops → OT progression; show the `Hello!` example.
- [ ] Mention client-side OT.
- [ ] Cursor/presence in memory, broadcast over same socket.
- [ ] Deep dive 1: consistent hashing + ZooKeeper + redirect; call out failure/rebalance handling.
- [ ] Deep dive 2: snapshot/compaction.
- [ ] Have a sentence ready for read-only, versioning, offline.

---

## References
- Operational Transformation — Wikipedia
- CRDT — Wikipedia; Yjs (open-source CRDT library)
- Canva: Real-time mouse pointers with WebRTC
- HelloInterview: Real-time Updates pattern; Design WhatsApp (similar socket-routing problem)
