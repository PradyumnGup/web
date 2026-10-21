# Notification System — System Design

> Source: Hello Interview, "Notification System" by Evan King (Jul 28, 2026) · Difficulty: Medium
> Patterns: Multi-step Processes · Scaling Writes

---

## Table of Contents

1. [Understanding the Problem](#understanding-the-problem)
2. [Functional Requirements](#functional-requirements)
3. [Non-Functional Requirements](#non-functional-requirements)
4. [Core Entities](#core-entities)
5. [API / System Interface](#api--system-interface)
6. [High-Level Design](#high-level-design)
   - [1. Send a notification (immediate or scheduled)](#1-send-a-notification-to-a-user-immediately-or-scheduled)
   - [2. Send campaigns to segments](#2-send-campaigns-to-segments-of-users)
   - [3. User notification preferences](#3-users-should-be-able-to-set-notification-preferences)
7. [Deep Dives](#deep-dives)
   - [1. Never drop an accepted notification](#1-how-do-we-guarantee-an-accepted-notification-is-never-dropped)
   - [2. High-priority delivery within 5 seconds](#2-how-do-we-deliver-high-priority-notifications-within-5-seconds-even-during-a-campaign-blast)
   - [3. Deduplication on top of at-least-once](#3-how-do-we-prevent-duplicate-notifications-on-top-of-at-least-once-delivery)
8. [Final Design](#final-design)
9. [Additional Deep Dives](#additional-deep-dives-to-consider)
10. [What Is Expected at Each Level](#what-is-expected-at-each-level)
11. [Knowledge Check](#knowledge-check)
12. [Notable Community Discussion](#notable-community-discussion)

---

## Understanding the Problem

### What is a Notification System?

A notification system is an **internal platform** that other teams at a company use to send messages to users across channels like **push, email, and SMS**. Product services hand it a message and a recipient, and the platform takes care of actually delivering it.

This is a **platform problem** rather than a user-facing product, so our customers are other engineering teams inside the company. Example internal clients:

- The **auth service** sending one-time passwords (OTPs)
- The **marketing team** blasting a promo to a million users at once

---

## Functional Requirements

### Core Requirements

1. Upstream services should be able to send a notification to a user via **push, email, or SMS**, either **immediately or in the future**.
2. Upstream services should be able to send **campaigns** — the same message delivered to a whole **segment** of users — immediately or scheduled.
3. Users should be able to set **notification preferences** (channel opt-outs and quiet hours).

### Below the Line (Out of Scope)

- Template management and rich content authoring
- Delivery analytics dashboards (open rates, click-through tracking)
- In-app notification feeds and badge counts
- Frequency capping across notification types

> **Tip:** You don't get much credit for enumerating "below the line" requirements — skip these if low on time.

---

## Non-Functional Requirements

### Sizing the Problem

Before writing these down, we need a number to design against.

| Metric | Value |
|---|---|
| Daily notifications | 10M / day |
| Sustained average | ~100 / sec (almost nothing) |
| Campaign surge: 1M in 5 min | > 3,000 / sec (single campaign) |
| **Design target** | **~5,000 / sec (~50x sustained)** |

Notifications don't spread evenly. If a client schedules a campaign at 9am they expect most of it delivered at that time, not dragged out through the day.

### Core Requirements

1. **At-least-once delivery with best-effort deduplication.** We'd rather send a notification twice accidentally than never at all.
2. **Handle surges of ~5,000 notifications per second.**
3. **Deliver high-priority notifications** (OTPs, security alerts) **within 5 seconds** of acceptance, even during a surge.

### Below the Line (Out of Scope)

- Strict ordering of notifications across channels
- Compliance workflows around consent and spam regulation
- Monitoring, alerting, and CI/CD

> **Note:** These requirements aren't independent — best-effort deduplication makes surges harder to handle. Point out facts like this to your interviewer.

### Two Traffic Classes

| Class | Examples | Volume | Latency Sensitivity |
|---|---|---|---|
| **High priority** | OTPs, security alerts | Low | Very high — a login code 20 minutes late is useless |
| **Standard** | Promos, digests | Huge bursts | Low — nobody cares if it lands 10 minutes late |

Getting both through one system is the heart of this problem. It's why the API asks callers to **declare a priority on every send**.

### Whiteboard Summary

```
Functional Requirements:
1. Send a notification to a user either immediately or scheduled
2. Send campaigns, immediately or scheduled.
3. Users should be able to set notification preferences

Non-functional Requirements:
1. At-least-once delivery with best-effort deduplication
2. Handle surges of ~5,000 notifications per second
3. Deliver critical notifications within 5 seconds, even during a surge.
```

---

## Core Entities

| Entity | Description |
|---|---|
| **Notification** | A single message to a single user on a single channel, along with its current status. E.g. a marketing email to John at 4pm. |
| **Campaign** | A scheduled send to a group of users. Captures delivery time, channel (SMS/email/push), and an audience. |
| **Segment** | A named audience like "all users in Canada". Membership is maintained by whatever tool defines the audience; we read it when a campaign fans out. |
| **User** | The recipient, along with their notification preferences and contact info. |
| **Template** | Message template referenced by campaigns. |

> **Pattern — definition vs. instances:** A Notification is like an instance of a Campaign (class). The same shape appears in Job Scheduler (recurring job vs. executions) and calendar systems (event vs. occurrences).

```
Entities:
- Notification
- Campaign
- Template
- Segment
- User
```

---

## API / System Interface

### Send a single notification

```http
POST /notifications -> 200 OK { id, status }
Body: {
  userId,
  channel,     // push | email | sms
  priority,    // high | standard
  content,     // { title, body }
  scheduledAt  // optional, defaults to now
}
```

We POST because we're creating a new resource. Initially we assume the provider is called synchronously, so we can return a 200 with success/failure. Once delivery moves off the request path, this evolves to a `202 Accepted` (see deep dives).

### Create a campaign

```http
POST /campaigns -> 202 Accepted { id }
Body: {
  segmentId,
  channel,     // push | email | sms
  priority,    // high | standard
  templateId,
  scheduledAt  // optional, defaults to now
}
```

Campaigns get their own endpoint rather than overloading `POST /notifications` with a `segmentId`. A campaign answers with a **202** right away — sending happens later, so there's no outcome to report at creation time.

> Both bodies grow an **idempotency key** before we're done. Start with the simple contract and evolve it, but tell your interviewer that's what you're doing.

### Set user preferences

```http
PUT /users/{userId}/preferences -> Preferences
Body: {
  optOuts,     // e.g. ["sms"]
  quietHours   // e.g. { start: "22:00", end: "08:00", tz }
}
```

Preferences are a full replace of a known resource, so **PUT** is idempotent for free.

> **Why `userId` in the path is OK here:** Normally user identity comes from the JWT, never the client. But our callers aren't end users — they are **internal services authenticated with API keys**, acting on behalf of users they've already authenticated. Platform/internal-tools interviewers may ask how authorization carries through (delegation tokens, service accounts).

### Register a push device token

```http
PUT /users/{userId}/devices -> Device
Body: {
  platform,   // ios | android
  pushToken
}
```

Push tokens rotate, so the product's mobile app sends them to us directly (on login and whenever they change) rather than syncing from the user service.

### Query notification status (added after async delivery)

```http
POST /notifications      -> 202 Accepted { id }
GET  /notifications/{id} -> { id, status, updatedAt }
```

---

## High-Level Design

Go one-by-one through the functional requirements, building the simplest thing that satisfies each. Resist solving the non-functional requirements now — get a functional system working first.

### 1) Send a notification to a user, immediately or scheduled

#### Direct email send

```
Upstream Service (Order/Auth)
        │
        ▼
   API Gateway  ── authenticates API key, rate limits
        │
        ▼
Notification Service ──► Notifications DB (Postgres)
        │                  ├─ Notification { userId, channel, message, status, ... }
        │                  └─ User { name, email, ... }
        ▼
   SMTP (Email Provider)
```

**Components:**

- **API Gateway:** Entry point for upstream services. Authenticates each service's API key and applies rate limits so a runaway service doesn't break high-priority use cases like authentication.
- **Notification Service:** Core of the system. Looks up the recipient's address, persists the notification, and calls the provider.
- **Database:** Stores Notification rows plus our slice of user data (email address, etc.) synced from the product's user service. Postgres, though any modern DB works.
- **Email Provider:** Actually gets the message into the inbox. We don't own the last mile — we hand it well-formed requests over SMTP or its API.

**Flow:**

1. Service POSTs to `/notifications` with `userId`, `channel`, `content`.
2. API Gateway authenticates the API key and forwards to the Notification Service.
3. Notification Service looks up the user's email address.
4. Service writes a Notification row with status `PENDING`, then calls the provider.
5. Service updates the row to `SENT` or `FAILED` based on the provider's response and returns `200` with id and status.

#### Adding SMS and Push

```
Notification Service ──► Twilio (SMS)
                     ──► SMTP (Email)
                     ──► APNs / FCM (Push)

User { name, email, phoneNumber, pushTokens, ... }
```

SMS follows the same flow with a phone number. Push requires a **device token** for APNs (Apple) or FCM (Google). Tokens rotate, so the mobile app registers them directly via `PUT /users/{userId}/devices`. Once stored, a push send works just like email — look up a token instead of an address.

> **Most common mistake:** Conflating the notification platform with the delivery provider. **Your system is a router and a bookkeeper.** It decides what to send where and records what happened. Carrier relationships and device connections belong to APNs, Twilio, and the email provider. Don't design persistent socket connections to phones.

#### Scheduled sends

To schedule a send, the Notification Service writes the row with status `SCHEDULED` and stops. A **Scheduler Cron Job** scans the Notifications table every minute for scheduled rows whose time has arrived and pushes each through the same send path.

```
Scheduler Cron Job ──(poll every 1 min)──► Notifications DB
        │
        └──► same send path as send-now
```

Minute granularity is fine. Anything that cares about seconds (OTPs) comes in synchronously and never touches the cron job.

### 2) Send campaigns to segments of users

When a caller POSTs to `/campaigns`, we write a Campaign row and return `202`. It's just CRUD until we turn that row into deliveries. Extend the scheduler cron to also point at the Campaigns table.

**Fan-out flow:**

1. Cron job wakes up and finds a Campaign whose `scheduledAt` has passed.
2. Reads the segment's membership to resolve the recipient list.
3. For each recipient, pushes a send through the same path a direct notification takes.
4. Marks the campaign complete.

```
Tables:
  notifications { userId, channel, message, status, ... }
  campaigns     { campaignId, target, messageTemplate, channel, scheduledTime, status, users, ... }
  segments      { segmentId, userId }
```

> **Snapshot semantics:** We read the segment as a snapshot when fan-out starts. Someone who joins mid-publish misses this campaign and catches the next one. Fine for marketing, but **acknowledge it** — noticing issues like this separates senior from junior candidates.

#### Three problems with this single pipeline

1. **Provider call is in the request path.** If Twilio has a slow day, callers wait on it. Worse, if the service crashes after writing the row but before calling the provider, the notification is gone. So much for at-least-once.
2. **A big campaign fights everything else for resources.** When the cron job expands a 1M-user campaign, all million sends may sit ahead of an OTP.
3. **A single host makes a million provider calls.** The whole fan-out is one loop on one machine. If it dies halfway through, we have no idea where it stopped.

### 3) Users should be able to set notification preferences

No new components needed. Preferences are attributes on the User, written through the preferences endpoint and routed by the Gateway to the Notification Service.

```
user {
  userId,
  optOutChannels,
  quietHoursStart,
  quietHoursEnd,
  timezone,
  ...
}
```

- Users get an **opt-out per channel** and a **quiet hours window**.
- Quiet hours apply to **standard** notifications but **not** high-priority ones (OTPs, fraud alerts).
- Every notification is checked against preferences **right before the provider call**.
- Suppressed notifications are recorded as `SUPPRESSED` with a reason rather than dropped silently — invaluable when oncall asks "why didn't this user get this notification?!"

With that we have a working system: a single OTP goes out in one synchronous hop, a million-user campaign fans out through the cron job, and users control what reaches them. **It works, but it doesn't satisfy a single non-functional requirement.**

---

## Deep Dives

### 1) How do we guarantee an accepted notification is never dropped?

Right now the Notification Service writes the row, calls the provider inline, updates the status, and then returns. We need to get **delivery off the request path** — the question is what we put it on.

> **Pattern — Multi-step Processes:** Notification delivery is accept → enqueue → deliver → record, where every step can fail independently. We won't use Temporal here (interviewers will wave you off because it obscures the interesting parts), but we're solving the same workflow problems.

#### ❌ Approach A: Synchronous call with retries

Keep the synchronous call and wrap it in retries with backoff before giving up.

**Fixes:** A single flaky provider response no longer drops a notification.

**Challenges:**
- Retry loop lives in process memory — a crash or routine deploy still loses everything.
- During a real provider outage the caller's connection is held open for the backoff; the auth service starts timing out on logins because our SMS provider is degraded. Good design fails fast and loud; this does neither.
- The caller can't tell "we gave up" from "we never got it." This is **at-most-once** — the opposite of what we promised.

#### ⚠️ Approach B: Decouple with a Postgres-backed queue

Acknowledge as soon as the notification is safely recorded; deliver separately.

- Notification Service writes the row with status `PENDING` and responds `202`.
- Background dispatchers poll for `PENDING` rows, call the provider, mark `SENT`/`FAILED`.
- Failed rows get picked up on a later poll. A crashed dispatcher loses nothing — the work is still in the table.

```http
POST /notifications      -> 202 Accepted { id }
GET  /notifications/{id} -> { id, status, updatedAt }
```

**Challenges:**
- We've built a queue on top of Postgres, and Postgres makes a mediocre queue.
- Multiple dispatchers can pick up the same row → double send. Need `SELECT ... FOR UPDATE SKIP LOCKED` to claim rows.
- Must build retries ourselves (attempt count, next-try time).
- Table becomes both queue and history; every poll works around a growing pile of sent/failed rows unless pruned or partitioned.
- Delivery latency floor = poll interval (10s poll → up to 10s wait).

#### ✅ Approach C: Durable message queue (SQS)

Stop building a bad queue; use a real one.

- Notification Service hands the notification to a **durable queue** and returns `202` once the queue acknowledges. The accept path writes **only to the queue**, not the DB.
- If we crash before the enqueue, the caller gets an error and retries.

**Delivery flow:**

1. A worker receives a message from the queue, which hides it from other workers for a **visibility timeout**.
2. Worker looks up the recipient's address/device token and checks preferences.
3. Worker calls the provider.
4. Worker writes the outcome to a Notification row in Postgres — `SENT` if accepted, `FAILED` if not (a FAILED row records the attempt and leaves the message eligible for retry).
5. **Only then** does it delete the message from the queue.

**Failure handling:**
- Worker dies mid-flight → message stays in queue → visibility timeout expires → another worker picks it up.
- Provider failures retry with **exponential backoff**.
- After too many failures → **dead-letter queue (DLQ)** for inspection.
- The queue handles retry policy, visibility timeout, and DLQ for us via configuration.

```
Upstream Service ──► API Gateway ──► Notification Service
                                          │         │
                              write campaigns       │ enqueue
                              & preferences         ▼
                                          │    Queue w/ DLQ
                                          ▼         │
                                   Notifications DB ◄──── Workers ──► Twilio / SMTP / APNs-FCM
                                   (read preferences,
                                    write SENT/FAILED/SUPPRESSED)

Scheduler Cron Job ──► Notifications DB (campaigns, scheduled) ──► enqueue
```

**Challenges:**
- More duplication than before: SQS is **at-least-once** and can produce duplicates internally, plus callers may create their own. We need dedup (deep dive 3).
- Consuming is **destructive**. Once a worker deletes a message we forget it. If we ship a bad worker build we can't rewind and replay.

> **On DB-first-then-queue:** Many candidates propose writing to the DB first and publishing to the queue second. This introduces a **dual-write problem** — a crash between steps leaves a row no message represents. The **outbox pattern** fixes that, but it's overkill here: accepting a notification is a single publish and nothing else.

> **On SQS vs. Kafka:** Kafka works too. Its strongest case is **rewind and replay** — ship a bad worker build, reset the offset, reprocess this morning's sends. A real advantage, but not something our requirements asked for, so we stayed with SQS.

> **Pattern — Scaling Writes:** A campaign blast is a write surge. Buffering it in a durable queue is one of the key strategies for scaling writes.

### 2) How do we deliver high-priority notifications within 5 seconds, even during a campaign blast?

**This is the crux of the interview.** Everything so far treated all notifications the same way.

**The problem:** Marketing sends a campaign to 1M users at 9am. All million go on the queue almost immediately, but providers only let us send **~5,000/sec across three channels**. The queue takes a little over 3 minutes to clear — fine for a promo. But then the auth service accepts a login OTP seconds later. It lands **a million messages back** in the same queue and waits **200 seconds**.

**Solution: Priority tier isolation**

- Separate **High Priority Queue** and **Standard Queue**, each with its own **worker pool**.
- High-priority workers never wait behind campaign traffic.
- Per-tier **provider budgets** (token buckets in Redis) so a standard surge can't consume the provider rate limit that high-priority sends need.
- The ceiling is **the providers**, not our own workers — adding worker capacity doesn't help past provider rate limits.

#### Surviving a provider brownout

A slow provider is the other way to blow the 5-second SLA. Say Twilio's p99 climbs from under a second to 30 seconds. Each stuck call pins a worker for 30 seconds. The job is to **keep high-priority messages moving** and let everything else wait it out.

| Technique | How it works |
|---|---|
| **Circuit breaker** | Track failure/timeout rate per provider. Once it crosses a threshold the breaker opens — calls fail immediately instead of waiting out a 30s timeout — and we shed non-essential load to leave room for high-priority messages. |
| **Bulkhead** | Cap how many in-flight calls to one provider at once (a semaphore). When Twilio has its allotment outstanding, no more workers start a call to it, so we aren't spinning all capacity on a service that's mostly down. |
| **Provider router** | Give channels with substitutes a primary and a backup. Can get clever in the UI too — if SMS is down, maybe email is the only auth option offered. Failover is generally only worth the cost for the **high-priority tier**. |

### 3) How do we prevent duplicate notifications on top of at-least-once delivery?

Every decision so far correctly favored at-least-once. The downside: duplicates are now more likely.

**Where duplicates enter:**

1. **Worker crash after provider call, before queue delete.** The SMS was sent, but the queue doesn't know. Visibility timeout expires → another worker sends it again.
2. **Caller retries.** An upstream service POSTs, times out, and POSTs again.
3. **Cron fan-out crash.** The cron job dies halfway through fanning out a segment; on rerun everything already enqueued goes back on the queue.

**Approach: at-least-once + deduplication at each entry point**

- **Idempotency key on the API.** Callers include a key on `POST /notifications` and `POST /campaigns`; the service rejects/short-circuits duplicates.
- **Dedup cache in Redis** at the worker, keyed on notification id / idempotency key, checked before the provider call.
- **Ordering matters:** Claim the dedup key **after** the provider call succeeds, not before. Claiming it *before* the provider call means a crash between claim and send loses the notification forever — turning at-least-once into **at-most-once**. Mark-after-send keeps the at-least-once guarantee with best-effort dedup.
- **Campaign fan-out resumability:** Track fan-out progress (e.g., checkpoint by batch/cursor) so a restarted cron resumes where it stopped rather than from the start.

> **Name your delivery semantics explicitly.** True exactly-once is impossible — it stops at the provider boundary. The honest design is at-least-once with deduplication at each point duplicates can enter. You should be able to point at each of those points on your diagram.

**Failure mode judgment:** What if the dedup store is down? Prefer **fail open** (risk a duplicate) over **fail closed** (risk dropping), consistent with the at-least-once requirement.

---

## Final Design

```
                           ┌──────────────────────────────────────────────────────────┐
                           │                     Notifications DB                     │
                           │  user          { userId, optOutChannels, quietHours*,    │
                           │                  timezone, email, phone, pushTokens }    │
                           │  notifications { userId, channel, message, status, ... } │
                           │  campaigns     { campaignId, target, messageTemplate,    │
                           │                  channel, scheduledTime, status, ... }   │
                           │  segments      { segmentId, userId }                     │
                           └───────▲──────────────────────────────▲───────────────────┘
                                   │ write campaigns              │ read preferences,
                                   │ & preferences                │ write SENT/FAILED/SUPPRESSED
Upstream Service                   │                              │
 (Order/Auth) ──► API Gateway ──► Notification Service            │
                                   │                              │
                     ┌─────────────┴──────────────┐               │
                     ▼                            ▼               │
          High Priority Queue             Standard Queue          │
                     │                            │               │
                     ▼                            ▼               │
          High Priority Workers           Standard Workers ───────┘
                     │                            │
                     └──────────┬─────────────────┘
                                │            ┌─────────────────────┐
                                ├──────────► │ Redis               │
                                │            │  - token bucket     │
                                │            │    (per-tier budget)│
                                │            │  - dedup cache      │
                                │            └─────────────────────┘
                                ▼
            ┌─────────────┬──────────────┬──────────────┐
            │ Twilio (SMS)│ SMTP (Email) │ APNs / FCM   │
            └─────────────┴──────────────┴──────────────┘

Scheduler Cron Job ──► polls campaigns & scheduled notifications ──► enqueues to the appropriate queue
```

**Key design decisions recap:**

| Concern | Decision |
|---|---|
| Durability | Accept path = single write to a durable queue (SQS) with DLQ; worker deletes message only after recording outcome |
| Priority isolation | Separate queues + worker pools per tier; per-tier provider token buckets in Redis |
| Provider brownout | Circuit breaker, bulkhead, provider router (failover for high-priority tier) |
| Deduplication | Idempotency keys at the API; Redis dedup cache at the worker, claimed **after** the provider call |
| Preferences | Checked at dispatch; suppressed sends recorded as `SUPPRESSED` with a reason |
| Scheduling | Cron scans at minute granularity; OTPs bypass it entirely |
| Status | `POST` returns `202`; `GET /notifications/{id}` for outcome |

---

## Additional Deep Dives to Consider

- **Send-time optimization.** Instead of delivering at 9am sharp, pick a per-user send time within a window marketing allows, based on when each user actually engages. The pipeline already handles millions of small scheduled sends; the interesting part is integrating with an offline ML model that scores send times.
- **Frequency capping.** Cap how many notifications a user gets per day across all types. Start next to the preference check at dispatch time; requires discussing counters.
- **Campaign cancellation.** Marketing schedules a blast, then legal wants it stopped (or there's a typo). Rare but serious. How do we check for cancellations without dragging on performance?
- **Timezone-aware delivery.** "9am" usually means 9am *for the user*, so a campaign spreads over 24 hours. The existing scheduling machinery fits — just with two dozen due times instead of one.

---

## What Is Expected at Each Level?

### Mid-level (E4)

- Clearly defined API and data model: send and campaign endpoints each carrying a priority; a Campaign that fans out into Notifications.
- A working high-level design covering both send-now and scheduled campaigns.
- Recognize that the synchronous provider call can drop notifications, and with some prompting land a good durable-delivery solution.
- Priority isolation and dedup ordering are not expected without help.

### Senior (E5)

- Speed through the high-level design to spend time in deep dives.
- Drive both **durable delivery** and **high-priority tier isolation** without steering.
- Confidently discuss finer details of isolation, queues, and prioritization; think ahead to potential problems.
- Probably won't reach per-tier provider budgets or dedup ordering unprompted — that's fine.

### Staff+

- Depth across all three deep dives.
- Unprompted, put numbers on the problem to see where the issues lie.
- Know the **ceiling is the providers**, not our workers — which stops us from just adding capacity at every step.
- Judgment in failure modes:
  - What should the system do when the dedup store is down?
  - When is failover to a backup provider worth its cost? (High-priority tier only.)
  - Why does claiming an idempotency key *before* the provider call turn at-least-once into at-most-once?
- The best staff candidates teach the interviewer something — usually an operational constraint that never shows up on a diagram.

---

## Knowledge Check

**Q1.** Which channel needs contact details the mobile app sends us directly rather than data synced over from the product's user service?

1. SMS, since carrier routing details change per region
2. All three, since our slice of user data is written only by the client apps
3. **Push, because device tokens rotate and there's nothing stable to sync** ✅
4. Email, because address changes have to be verified before we can use them

---

## Notable Community Discussion

- **Campaign fan-out parallelism (Abhinav Tripathi):** The article doesn't detail how a 100M-user campaign fans out. Sequentially fetching batches of 1,000 users could take hours — batches should be fetched and enqueued in parallel.
- **Temporal / durable workflows (BrightRoseAnteater643 → Stefan Mai):** Spawning parallel child workflows per batch gives resumability and checkpointing. Stefan's counterpoint: one workflow per notification would stress even hardened durable-execution frameworks at this scale; a single workflow loses parallelism; "shard" workflows start to feel hacky. A purpose-built design like the one above is more in line with real-world practice and what interviewers expect.
- **Kafka for time-sensitive campaigns (pradeep garimella):** For 100M users, a Kafka cluster with ~100 partitions lets the Notification Service batch and round-robin writes (~1M per partition). Worth clarifying this as an NFR upfront.
- **Correction:** An earlier version said "SQS is at-most-once"; this was fixed — SQS is **at-least-once**.
