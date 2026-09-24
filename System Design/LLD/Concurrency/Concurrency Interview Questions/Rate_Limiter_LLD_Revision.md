Rate Limiter – LLD Revision Notes

_Amazon Low-Level Design interview prep | Java | Difficulty: Hard_

_Full working code is in my GitHub repo. This doc only has the skeletons and the "why" behind each decision so I can revise quickly before the interview._

# 0\. The 60-second summary

- **Problem:** In-memory rate limiter for an API gateway. Config per endpoint chooses an algorithm + algorithm-specific params. Request = (clientId, endpoint). Return (allowed, remaining, retryAfterMs).
- **Entities:** RateLimiter (facade/orchestrator), Limiter (interface / strategy), LimiterFactory (creation), RateLimitResult (immutable value object).
- **Patterns:** Factory (heterogeneous config -> right Limiter) + Strategy (pluggable algorithms).
- **Algorithms to know:** Token Bucket (implement first), Sliding Window Log (second). Mention Fixed Window & Sliding Window Counter as alternatives.
- **Extensions to be ready for:** new algorithm, dynamic config, thread safety (per-key lock), memory eviction.

# 1\. Problem statement (as given)

"You're building an in-memory rate limiter for an API gateway. The system receives configuration from an external service that provides rate limiting rules per endpoint. Each endpoint can have its own limit with a specific algorithm."

```
{
  "endpoint": "/search",
  "algorithm": "TokenBucket",
  "algoConfig": {
    "capacity": 1000,
    "refillRatePerSecond": 10
  }
}
```

Meaning: bursts up to 1000 requests, refilling at 10 req/sec.

# 2\. Clarifying questions to ask (and expected answers)

| **Question I should ask**                              | **Likely answer**                                               | **What it tells me**                                      |
| ------------------------------------------------------ | --------------------------------------------------------------- | --------------------------------------------------------- |
| Do different algorithms have different parameter sets? | Yes. algoConfig always exists but its contents vary.            | Config is heterogeneous -> need a Factory.                |
| What does a request carry?                             | clientId (string) + endpoint (string).                          | Request is just method params, not an entity.             |
| What do we return?                                     | allowed, remaining, retryAfter (if denied).                     | Need a structured result type, not a boolean.             |
| Endpoint with no config?                               | Fall back to a default config. Never reject for missing config. | Need a defaultLimiter.                                    |
| Concurrency?                                           | "Don't worry about it to start."                                | Build clean first; be ready to add per-key locking later. |
| Distributed or single process?                         | Single process, in-memory.                                      | No Redis / coordination.                                  |
| Is config dynamic?                                     | Loaded once at startup.                                         | No hot reload (but be ready to discuss it).               |

**Interview tip:** When the interviewer says "don't worry about X", they usually circle back to X later. Build a clean foundation and keep a mental note of where X would slot in.

# 3\. Final requirements (write these on the whiteboard)

1. Configuration is provided at startup (loaded once).
2. System receives requests with (clientId: String, endpoint: String).
3. Each endpoint config specifies: algorithm name + algorithm-specific params.
4. Enforce rate limits by checking clientId against the endpoint's configuration.
5. Return RateLimitResult(allowed: boolean, remaining: int, retryAfterMs: Long | null).
6. Endpoint with no config -> use a default limit.

**Out of scope:**

- Distributed rate limiting (Redis, coordination)
- Dynamic configuration updates
- Metrics / monitoring
- Config validation beyond basic checks

# 4\. Core entities – pruning the nouns

| **Candidate**                     | **Entity?**   | **Reasoning**                                                                                                   |
| --------------------------------- | ------------- | --------------------------------------------------------------------------------------------------------------- |
| Request                           | No            | External. We receive two strings and use them immediately as lookup keys.                                       |
| Client                            | No            | External. clientId is just a map key for per-client state.                                                      |
| Endpoint                          | No            | Just a label / string key to find the right Limiter.                                                            |
| Rate Limiting Algorithm (Limiter) | YES           | Has its own config, its own per-key state, its own allow/deny logic. Different algorithms = different classes.  |
| RateLimiter                       | YES           | Orchestrator / entry point. Looks up endpoint config, delegates to the right Limiter, handles default fallback. |
| RateLimitResult                   | YES           | Value object bundling (allowed, remaining, retryAfterMs). Immutable.                                            |
| LimiterFactory                    | YES (derived) | Emerges from the need to build the right Limiter from heterogeneous config.                                     |

Relationships: RateLimiter owns Map&lt;String, Limiter&gt; (endpoint -> limiter) + a defaultLimiter. LimiterFactory builds Limiters. Each Limiter owns its own per-client state map.

# 5\. Class design (Java skeletons)

## 5.1 Limiter – the Strategy interface

```
public interface Limiter {
    RateLimitResult allow(String key);   // key = clientId
}
```

**Why an interface and NOT an abstract base class?**

Each algorithm needs fundamentally different per-key state: Token Bucket -> (tokens: double, lastRefillTime: long); Sliding Window Log -> Queue&lt;Long&gt; of timestamps; Fixed Window -> (count, windowStart). There is no shared state or shared helper to hoist up, so an abstract class would be an empty shell = "an interface with extra steps".

## 5.2 RateLimitResult – immutable value object

```
public final class RateLimitResult {
    private final boolean allowed;
    private final int remaining;       // quota left AFTER this request
    private final Long retryAfterMs;   // null when allowed; > 0 when denied
    public RateLimitResult(boolean allowed, int remaining, Long retryAfterMs) { ... }
    public boolean isAllowed() { ... }
    public int getRemaining() { ... }
    public Long getRetryAfterMs() { ... }
}
```

- Nullable retryAfterMs is cleaner than a 0 sentinel.
- Unit in the field name (Ms) avoids ambiguity (ms vs sec vs absolute timestamp).
- remaining = quota left after processing this request (limit 100, 3rd request -> 97).
- In Java 16+ a record works too: record RateLimitResult(boolean allowed, int remaining, Long retryAfterMs) {}

## 5.3 LimiterFactory – Factory pattern

```
public class LimiterFactory {
    public Limiter create(Map<String, Object> externalConfig) {
        String algorithm = (String) externalConfig.get("algorithm");
        Map<String, Object> algoConfig =
            (Map<String, Object>) externalConfig.get("algoConfig");
        switch (algorithm) {
            case "TokenBucket":
                return new TokenBucketLimiter(
                    (int) algoConfig.get("capacity"),
                    (int) algoConfig.get("refillRatePerSecond"));
            case "SlidingWindowLog":
                return new SlidingWindowLogLimiter(
                    (int) algoConfig.get("maxRequests"),
                    ((Number) algoConfig.get("windowMs")).longValue());
            default:
                throw new IllegalArgumentException("Unknown algorithm: " + algorithm);
        }
    }
}
```

- **Why a factory?** Multiple classes implement the same interface and the choice depends on runtime data (the "algorithm" discriminator). Centralises creation in one place instead of scattering switch statements.
- **Fail fast:** Unknown algorithm -> throw, don't silently pick a default.
- **Evolution:** Could become a Registry (Map&lt;String, Function<Map,Limiter&gt;>) if algorithms are pluggable at runtime. For 2 algorithms in an interview, a switch is clearer.

## 5.4 RateLimiter – the facade

```
public class RateLimiter {
    private final Map<String, Limiter> limiters;   // endpoint -> limiter
    private final Limiter defaultLimiter;
    public RateLimiter(List<Map<String, Object>> configs,
                       Map<String, Object> defaultConfig) {
        LimiterFactory factory = new LimiterFactory();
        this.limiters = new HashMap<>();
        for (Map<String, Object> cfg : configs) {
            String endpoint = (String) cfg.get("endpoint");
            if (endpoint == null) continue;          // basic validation
            limiters.put(endpoint, factory.create(cfg));
        }
        this.defaultLimiter = factory.create(defaultConfig);
    }
    public RateLimitResult allow(String clientId, String endpoint) {
        Limiter limiter = limiters.getOrDefault(endpoint, defaultLimiter);
        return limiter.allow(clientId);
    }
}
```

- **One public method.** Don't add getConfig()/updateConfig() unless asked — YAGNI.
- **Eager instantiation:** All limiters built in the constructor. Simpler than lazy (no double-checked locking), negligible memory for dozens–hundreds of endpoints. Lazy only makes sense for thousands of mostly-idle endpoints.
- **Default fallback:** getOrDefault gives O(1) lookup + fallback in one line.

## 5.5 Final class diagram (text)

```
RateLimiter
  - limiters: Map<String, Limiter>
  - defaultLimiter: Limiter
  + RateLimiter(configs, defaultConfig)
  + allow(clientId, endpoint) -> RateLimitResult
        |  uses                       | delegates to
        v                             v
LimiterFactory                 <<interface>> Limiter
  + create(configData) -> Limiter     + allow(key) -> RateLimitResult
                                         ^              ^
                                         |              |
                          TokenBucketLimiter    SlidingWindowLogLimiter
                          - capacity            - maxRequests
                          - refillRatePerSecond - windowMs
                          - buckets: Map        - logs: Map
```

# 6\. Rate limiting algorithms

| **Algorithm**          | **Per-key state**                      | **Tradeoff**                                                 |
| ---------------------- | -------------------------------------- | ------------------------------------------------------------ |
| Token Bucket           | (tokens: double, lastRefillTime: long) | Allows bursts up to capacity, smooth refill. Used by Stripe. |
| Sliding Window Log     | Queue&lt;Long&gt; timestamps           | Perfect accuracy; memory = O(requests in window).            |
| Fixed Window Counter   | (count, windowStart)                   | Simplest; suffers boundary effect (2x burst at window edge). |
| Sliding Window Counter | (currentCount, prevCount, windowStart) | Approximation; balanced accuracy vs memory.                  |

**Interview tip:** Implement ONE algorithm fully (Token Bucket). Only do a second if asked / time permits. Don't try to code all four.

## 6.1 TokenBucketLimiter

Mental model: each client has a bucket of capacity tokens. Tokens drip in at refillRatePerSecond. Each request consumes 1 token. No token -> deny. Refill is computed lazily on each request from elapsed time (no background thread).

```
public class TokenBucketLimiter implements Limiter {
    private final int capacity;                 // shared config
    private final int refillRatePerSecond;      // shared config
    private final Map<String, TokenBucket> buckets = new HashMap<>();  // per-client
    static class TokenBucket {
        double tokens;          // double: 0.5 tokens is a valid state
        long lastRefillTime;    // when we last computed refill
    }
    ...
}
```

**allow(key) – 4 steps:**

```
public RateLimitResult allow(String key) {
    // 1. get or create bucket (new bucket starts FULL, lastRefillTime = now)
    TokenBucket b = buckets.computeIfAbsent(key,
            k -> new TokenBucket(capacity, System.currentTimeMillis()));
    // 2. lazy refill based on elapsed time
    long now = System.currentTimeMillis();
    long elapsed = now - b.lastRefillTime;
    double tokensToAdd = (elapsed * refillRatePerSecond) / 1000.0;
    b.tokens = Math.min(capacity, b.tokens + tokensToAdd);
    b.lastRefillTime = now;
    // 3 + 4. check, then consume or compute retry
    if (b.tokens >= 1) {
        b.tokens -= 1;
        return new RateLimitResult(true, (int) Math.floor(b.tokens), null);
    } else {
        double tokensNeeded = 1 - b.tokens;
        long retryAfterMs = (long) Math.ceil((tokensNeeded * 1000) / refillRatePerSecond);
        return new RateLimitResult(false, 0, retryAfterMs);
    }
}
```

**Key formulas to remember:**

- **Refill:** tokensToAdd = elapsedMs \* rate / 1000; tokens = min(capacity, tokens + tokensToAdd)
- **Retry:** retryAfterMs = ceil((1 - tokens) \* 1000 / rate) e.g. tokens=0.3, rate=10 -> 70 ms
- **Why ceil?** Never tell the client to retry too early.
- **Why floor for remaining?** API returns whole requests.

**Talking points:**

- **Why no background refill thread?** Would iterate every bucket periodically – wasteful when most clients are idle. On-demand refill does work only when requests arrive. This is what production systems do.
- **Hidden memory leak:** buckets is never pruned. Needs eviction (see extension 4).
- **Edge case – huge elapsed:** Client idle for days -> tokensToAdd is huge, but min(capacity, ...) caps it. Fine.
- **Edge case – overflow:** elapsed \* rate is long \* int -> long; ok for realistic values, but call it out.

## 6.2 SlidingWindowLogLimiter

Mental model: keep the exact timestamp of every request per client. On each request, drop timestamps older than (now - windowMs), then compare queue size to maxRequests.

```
public class SlidingWindowLogLimiter implements Limiter {
    private final int maxRequests;
    private final long windowMs;
    private final Map<String, Deque<Long>> logs = new HashMap<>();  // per-client FIFO
    public RateLimitResult allow(String key) {
        Deque<Long> log = logs.computeIfAbsent(key, k -> new ArrayDeque<>());
        long now = System.currentTimeMillis();
        long cutoff = now - windowMs;
        // evict stale timestamps from the FRONT
        while (!log.isEmpty() && log.peekFirst() < cutoff) {
            log.pollFirst();
        }
        if (log.size() < maxRequests) {
            log.addLast(now);
            return new RateLimitResult(true, maxRequests - log.size(), null);
        } else {
            long oldest = log.peekFirst();
            long retryAfterMs = (oldest + windowMs) - now;   // when oldest ages out
            return new RateLimitResult(false, 0, retryAfterMs);
        }
    }
}
```

- **Why a queue / deque?** Add at back, remove from front = FIFO. O(1) both ends. A List would be O(n) on removeFirst.
- **Why peek then poll?** Inspect the oldest without removing; remove only if stale.
- **Retry math:** oldest=50000, window=60000, now=105000 -> ages out at 110000 -> retry in 5000 ms.
- **Cost:** Memory O(maxRequests) per active client. 1000 req/min per key = 1000 longs per key.

# 7\. Verification walk-through (do this out loud)

Setup: "/search" -> TokenBucket(capacity=10, refillRatePerSecond=1). Client "user123".

| **Time**      | **Bucket before**     | **Refill**                | **Decision**                                 | **Result**      |
| ------------- | --------------------- | ------------------------- | -------------------------------------------- | --------------- |
| t=0 (1st req) | created: tokens=10    | elapsed=0 -> +0           | 10 >= 1, consume -> 9                        | (true, 9, null) |
| t=500         | tokens=9              | 500\*1/1000 = +0.5 -> 9.5 | consume -> 8.5                               | (true, 8, null) |
| 10 rapid reqs | ...                   | ~0                        | bucket drains to ~0                          | ...             |
| t=1100, empty | tokens=0.1, last=1000 | 100\*1/1000 = +0.1 -> 0.2 | 0.2 &lt; 1 -&gt; deny; need 0.8 -> ceil(800) | (false, 0, 800) |

This checks refill, consumption, capping, and retry computation in one pass.

# 8\. Extensibility – the "what if" questions

## 8.1 Add a new algorithm (e.g. Fixed Window Counter)

Two steps, nothing else changes (Open/Closed principle):

```
// Step 1: new Strategy
public class FixedWindowCounterLimiter implements Limiter {
    private final int maxRequests;
    private final long windowMs;
    private final Map<String, WindowState> windows = new HashMap<>();
    static class WindowState { int count; long windowStart; }
    public RateLimitResult allow(String key) { /* reset count if new window, else count++ */ }
}
// Step 2: one new case in LimiterFactory.create(...)
case "FixedWindowCounter":
    return new FixedWindowCounterLimiter(
        (int) algoConfig.get("maxRequests"),
        ((Number) algoConfig.get("windowMs")).longValue());
```

## 8.2 Dynamic configuration updates

**Approach A – Rebuild & atomic swap (simple, loses state):**

```
public void reloadConfig(List<Map<String,Object>> configs, Map<String,Object> defaultConfig) {
    Map<String, Limiter> newLimiters = new HashMap<>();
    for (...) newLimiters.put(endpoint, factory.create(cfg));
    Limiter newDefault = factory.create(defaultConfig);
    this.limiters = newLimiters;        // fields become volatile / AtomicReference
    this.defaultLimiter = newDefault;   // atomic swap, no half-updated state
}
```

- \+ Reuses the startup path; handles add/remove/switch-algorithm uniformly.
- \- All per-key state is lost: a client at 90/100 gets a fresh 100. Bad if you are lowering limits to stop abuse.
- Fine when config changes are rare (once per deploy).

**Approach B – In-place update, preserve state (senior answer):**

```
public interface Limiter {
    RateLimitResult allow(String key);
    void updateConfig(Map<String, Object> algoConfig);   // NEW
}
// TokenBucketLimiter
public void updateConfig(Map<String, Object> algoConfig) {
    this.capacity = (int) algoConfig.get("capacity");
    this.refillRatePerSecond = (int) algoConfig.get("refillRatePerSecond");
    for (TokenBucket b : buckets.values())
        b.tokens = Math.min(b.tokens, capacity);   // clamp to new capacity
}
// RateLimiter
public void updateEndpointConfig(String endpoint, Map<String,Object> cfg) {
    Limiter l = limiters.get(endpoint);
    if (l != null) l.updateConfig((Map) cfg.get("algoConfig"));
    else limiters.put(endpoint, factory.create(cfg));   // new endpoint
}
```

- \+ Clients keep their token counts / request history; allows gradual rollout of limits.
- \- Each algorithm needs custom update logic; switching algorithm type still requires replacement (incompatible state).
- \- Needs synchronisation between updateConfig and allow (fields volatile; or a ReadWriteLock – allow() takes read lock, updateConfig() takes write lock so all params change atomically and are visible to all threads).

## 8.3 Thread safety – per-key locking

Race: two threads read tokens=1 for the same client, both decrement, two requests allowed with capacity for one.

Fix: ConcurrentHashMap + computeIfAbsent for atomic get-or-create, then synchronize on the bucket object itself.

```
private final ConcurrentHashMap<String, TokenBucket> buckets = new ConcurrentHashMap<>();
public RateLimitResult allow(String key) {
    TokenBucket bucket = buckets.computeIfAbsent(key,
        k -> new TokenBucket(capacity, System.currentTimeMillis()));
    boolean allowed; int remaining; Long retryAfterMs;
    synchronized (bucket) {                 // lock per CLIENT, not global
        long now = System.currentTimeMillis();
        double tokensToAdd = ((now - bucket.lastRefillTime) * refillRatePerSecond) / 1000.0;
        bucket.tokens = Math.min(capacity, bucket.tokens + tokensToAdd);
        bucket.lastRefillTime = now;
        if (bucket.tokens >= 1) {
            bucket.tokens -= 1; allowed = true;
            remaining = (int) Math.floor(bucket.tokens); retryAfterMs = null;
        } else {
            allowed = false; remaining = 0;
            retryAfterMs = (long) Math.ceil(((1 - bucket.tokens) * 1000) / refillRatePerSecond);
        }
    }
    return new RateLimitResult(allowed, remaining, retryAfterMs);  // build outside lock
}
```

- **Why per-key, not a global lock?** Client A and B never block each other; only same-client requests serialise.
- **Why sync on the bucket itself?** No separate lock map to manage; bucket lives in a ConcurrentHashMap so it is a stable monitor.
- **Why computeIfAbsent?** Atomic get-or-create; avoids two threads creating two buckets for the same key.
- **Alternative:** Make TokenBucket fields into an AtomicReference&lt;State&gt; with CAS loop (lock-free) – mention only if asked.
- **Config fields:** If updateConfig exists, mark capacity/refillRatePerSecond volatile or guard with ReadWriteLock.

## 8.4 Memory growth – eviction

- **Problem:** buckets / logs maps grow unbounded with unique clientIds.
- **Option 1 – TTL sweep:** Track lastAccessTime per key; background thread periodically removes entries idle > N minutes.
- **Option 2 – LRU:** Bounded LRU cache (LinkedHashMap accessOrder=true with removeEldestEntry, or Caffeine/Guava Cache with maximumSize / expireAfterAccess).
- **Option 3 – Distributed:** Redis keys with TTL – eviction is built in.
- **Consequence:** Evicted client's next request looks like a first request (full burst). Acceptable – active clients are never evicted because access time keeps refreshing.

# 9\. Alternative design to be aware of (from discussion threads)

**Limiter instance per (clientId + endpoint) instead of per endpoint:**

- RateLimiter holds Map&lt;"clientId::endpoint", Limiter&gt;; each Limiter is stateless w.r.t. clients (single bucket).
- \+ Algorithm class is simpler (no inner map); synchronisation moves to RateLimiter; easy to give different algos per client.
- \- Explodes object count (clients x endpoints), harder TTL management, in-place config updates must touch many instances.
- Know it so you can discuss the tradeoff if the interviewer suggests it; default to per-endpoint Limiter with internal per-key map.

**Why not a Singleton Limiter?**

Each endpoint has different capacity / rate, so one shared instance cannot work. Limiters are per-endpoint.

# 10\. Quick-fire Q&A cheat sheet

| **Question**                                 | **One-line answer**                                                                                   |
| -------------------------------------------- | ----------------------------------------------------------------------------------------------------- |
| Why interface, not abstract class?           | No common per-key state across algorithms; abstract class would be empty.                             |
| Why a factory?                               | Runtime discriminator ("algorithm") picks among several Limiter implementations; centralise creation. |
| Why eager instantiation?                     | Simpler (no lazy-init locking), trivial memory at 10s–100s of endpoints.                              |
| Why tokens as double?                        | Continuous refill; 0.5 tokens is legitimate state.                                                    |
| Why no refill thread?                        | Lazy refill on request does work only for active clients; simpler & standard in production.           |
| Why Deque for Sliding Window Log?            | O(1) add-last / remove-first; List removeFirst is O(n).                                               |
| Why nullable retryAfterMs?                   | Explicit absence beats 0 sentinel; unit suffix avoids ambiguity.                                      |
| Why per-key lock?                            | Different clients don't contend; only same-client requests serialise.                                 |
| Why does each Limiter own its per-key state? | State shape is algorithm-specific; RateLimiter stays a thin orchestrator.                             |
| Unknown endpoint?                            | getOrDefault -> defaultLimiter; never reject for missing config.                                      |
| Unknown algorithm?                           | Fail fast with IllegalArgumentException in the factory.                                               |
| Client idle for days?                        | tokensToAdd huge but Math.min(capacity, ...) caps it.                                                 |
| Timestamp overflow?                          | long millis is fine until year 292 million; but avoid int math on elapsed\*rate.                      |
| Fixed window's flaw?                         | Boundary effect: up to 2x limit around window edge.                                                   |

# 11\. What the interviewer expects at each level

| **Level**        | **Expectation**                                                                                                                                                                                                                                                                                                    |
| ---------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| Junior (SDE I)   | Break the problem down, build a working RateLimiter + Limiter + RateLimitResult, factory extracts params, algorithm tracks correct per-client state. Hints OK. Handle: limit exceeded, unknown algorithm.                                                                                                          |
| Mid (SDE II)     | Clean separation with little guidance. Config flows as raw data through the factory. Correct retry math. Justify: why factory? why per-limiter state? Discuss at least one extension (thread safety or dynamic config).                                                                                            |
| Senior (SDE III) | Boundaries obvious without deliberation. Proactively state tradeoffs (factory switch grows; lazy refill vs thread; per-key locking vs lock management). Catch edge cases unprompted (huge elapsed, overflow). Discuss simple -> Strategy/Registry evolution, distributed variant, eviction at scale. Finish early. |

# 12\. My 45-minute game plan

1. **0–5 min:** Clarify (7 questions above). Write requirements + out-of-scope.
2. **5–10 min:** Entities: prune Request/Client/Endpoint; keep RateLimiter, Limiter, RateLimitResult; derive Factory.
3. **10–20 min:** Class skeletons: Limiter interface, RateLimitResult, LimiterFactory switch, RateLimiter constructor + allow.
4. **20–33 min:** Implement TokenBucketLimiter.allow() step by step (create -> refill -> check -> consume/retry).
5. **33–37 min:** Verify with the t=0 / t=500 / empty-bucket trace.
6. **37–45 min:** Extensions: new algorithm -> dynamic config -> thread safety (per-key lock) -> eviction. Mention Sliding Window Log only if asked.

**Reminder:** Say the tradeoff out loud before the interviewer asks. "I'm choosing eager instantiation because... the alternative is lazy which would need..." – this is what separates mid from senior.