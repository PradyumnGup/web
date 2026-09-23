import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

class RateLimitResult {
    private final boolean allowed;
    private final int remaining;
    private final Long retryAfterMs;

    public RateLimitResult(boolean allowed, int remaining, Long retryAfterMs) {
        this.allowed = allowed;
        this.remaining = remaining;
        this.retryAfterMs = retryAfterMs;
    }

    public boolean isAllowed() {
        return allowed;
    }

    public int getRemaining() {
        return remaining;
    }

    public Long getRetryAfterMs() {
        return retryAfterMs;
    }
}


interface Limiter{
    RateLimitResult allow(String key);
}



class TokenBucketLimiter implements Limiter {
    private final int capacity;
    private final int refillRatePerSecond;
    private final ConcurrentHashMap<String, TokenBucket> buckets = new ConcurrentHashMap<>();

    public TokenBucketLimiter(int capacity, int refillRatePerSecond) {
        this.capacity = capacity;
        this.refillRatePerSecond = refillRatePerSecond;
    }

    public RateLimitResult allow(String key) {
        // Atomically get or create bucket
        TokenBucket bucket = buckets.computeIfAbsent(key, 
            k -> new TokenBucket(capacity, System.currentTimeMillis())
        );
        
        // Calculate result values while holding lock on bucket
        boolean allowed;
        int remaining;
        Long retryAfterMs;
        
        synchronized(bucket) {
            long now = System.currentTimeMillis();
            long elapsed = now - bucket.lastRefillTime;
            double tokensToAdd = (elapsed * refillRatePerSecond) / 1000.0;
            bucket.tokens = Math.min(capacity, bucket.tokens + tokensToAdd);
            bucket.lastRefillTime = now;
            
            if (bucket.tokens >= 1) {
                bucket.tokens -= 1;
                allowed = true;
                remaining = (int) Math.floor(bucket.tokens);
                retryAfterMs = null;
            } else {
                double tokensNeeded = 1 - bucket.tokens;
                allowed = false;
                remaining = 0;
                retryAfterMs = (long) Math.ceil((tokensNeeded * 1000) / refillRatePerSecond);
            }
        }
        
        // Construct result outside the lock
        return new RateLimitResult(allowed, remaining, retryAfterMs);
    }

    static class TokenBucket {
        double tokens;
        long lastRefillTime;
        
        TokenBucket(double initialTokens, long time) {
            this.tokens = initialTokens;
            this.lastRefillTime = time;
        }
    }
}
class SlidingWindowLogLimiter implements Limiter {

    private final int maxRequests;
    private final long windowMs;


    /*
     * clientId -> timestamps of requests
     *
     * Example:
     *
     * user1 -> [1000, 1200, 1500]
     * user2 -> [1100, 1400]
     */
    private final ConcurrentHashMap<String, Deque<Long>> requestLogs =
            new ConcurrentHashMap<>();


    public SlidingWindowLogLimiter(
            int maxRequests,
            long windowMs
    ) {

        if (maxRequests <= 0) {
            throw new IllegalArgumentException(
                    "maxRequests must be > 0"
            );
        }

        if (windowMs <= 0) {
            throw new IllegalArgumentException(
                    "windowMs must be > 0"
            );
        }

        this.maxRequests = maxRequests;
        this.windowMs = windowMs;
    }


    @Override
    public RateLimitResult allow(String key) {

        /*
         * Get or create this client's request log.
         */
        Deque<Long> timestamps =
                requestLogs.computeIfAbsent(
                        key,
                        k -> new ArrayDeque<>()
                );


        synchronized (timestamps) {

            long now = System.currentTimeMillis();

            // ------------------------------------------------
            // Step 1: Remove expired requests
            // ------------------------------------------------

            long windowStart = now - windowMs;

            while (!timestamps.isEmpty()
                    && timestamps.peekFirst() <= windowStart) {

                timestamps.pollFirst();
            }


            // ------------------------------------------------
            // Step 2: Check whether limit is reached
            // ------------------------------------------------

            if (timestamps.size() >= maxRequests) {

                long oldestTimestamp =
                        timestamps.peekFirst();

                long retryAfterMs =
                        (oldestTimestamp + windowMs) - now;

                return new RateLimitResult(
                        false,
                        0,
                        Math.max(0L, retryAfterMs)
                );
            }


            // ------------------------------------------------
            // Step 3: Allow request
            // ------------------------------------------------

            timestamps.addLast(now);

            int remaining =
                    maxRequests - timestamps.size();

            return new RateLimitResult(
                    true,
                    remaining,
                    null
            );
        }
    }
}

class LimiterFactory {
    @SuppressWarnings("unchecked")
    public Limiter create(Map<String, Object> config){
        String algorithm = (String) config.get("algorithm");
        Map<String, Object> algoConfig = (Map<String, Object>) config.get("algoConfig");
        if (algoConfig == null) {
            algoConfig = Collections.emptyMap();
        }

        if ("TokenBucket".equals(algorithm)) {
            int capacity = ((Number) algoConfig.getOrDefault("capacity", 0)).intValue();
            int refillRate = ((Number) algoConfig.getOrDefault("refillRatePerSecond", 0)).intValue();
            return new TokenBucketLimiter(capacity, refillRate);
        }

        if ("SlidingWindowLog".equals(algorithm)) {
            int maxRequests = ((Number) algoConfig.getOrDefault("maxRequests", 0)).intValue();
            long windowMs = ((Number) algoConfig.getOrDefault("windowMs", 0)).longValue();
            return new SlidingWindowLogLimiter(maxRequests, windowMs);
        }
        
        throw new IllegalArgumentException("Unknown algorithm: " + algorithm);
    }
}
class RateLimiter{
    
    private final Map<String,Limiter> limiters;
    private final Limiter defaultLimiter;

    public RateLimiter(List<Map<String, Object>> configs, Map<String, Object> defaultConfig) {
        this.limiters = new HashMap<>();
        LimiterFactory factory = new LimiterFactory();
        for(Map<String,Object> config : configs){
            String endpoint = (String) config.get("endpoint");
            if(endpoint == null){
                continue;
            }
            Limiter limiter = factory.create(config);
            limiters.put(endpoint,limiter);
        }
        this.defaultLimiter = factory.create(defaultConfig);
    }
    public RateLimitResult allow(String clientId, String endpoint) {
        Limiter limiter = limiters.getOrDefault(endpoint, defaultLimiter);
        return limiter.allow(clientId);
    }
}
class Main {
    public static void main(String[] args)throws InterruptedException {
        
        // ====================================================
        // Configuration for /search
        //
        // capacity = 3
        // refill = 1 token / second
        //
        // So initially:
        //
        // bucket = 3 tokens
        // ====================================================

        Map<String, Object> searchConfig =
                new HashMap<>();


        searchConfig.put(
                "endpoint",
                "/search"
        );


        searchConfig.put(
                "algorithm",
                "TokenBucket"
        );


        Map<String, Object> searchAlgoConfig =
                new HashMap<>();


        searchAlgoConfig.put(
                "capacity",
                3
        );


        searchAlgoConfig.put(
                "refillRatePerSecond",
                1
        );


        searchConfig.put(
                "algoConfig",
                searchAlgoConfig
        );


        // ====================================================
        // Configuration for /upload
        //
        // Maximum 2 requests in 5 seconds
        // ====================================================

        Map<String, Object> uploadConfig =
                new HashMap<>();


        uploadConfig.put(
                "endpoint",
                "/upload"
        );


        uploadConfig.put(
                "algorithm",
                "SlidingWindowLog"
        );


        Map<String, Object> uploadAlgoConfig =
                new HashMap<>();


        uploadAlgoConfig.put(
                "maxRequests",
                2
        );


        uploadAlgoConfig.put(
                "windowMs",
                5000L
        );


        uploadConfig.put(
                "algoConfig",
                uploadAlgoConfig
        );


        // ====================================================
        // Default configuration
        //
        // Any endpoint not explicitly configured will use
        // this limiter.
        // ====================================================

        Map<String, Object> defaultConfig =
                new HashMap<>();


        defaultConfig.put(
                "algorithm",
                "TokenBucket"
        );


        Map<String, Object> defaultAlgoConfig =
                new HashMap<>();


        defaultAlgoConfig.put(
                "capacity",
                2
        );


        defaultAlgoConfig.put(
                "refillRatePerSecond",
                1
        );


        defaultConfig.put(
                "algoConfig",
                defaultAlgoConfig
        );

        // ====================================================
        // Create RateLimiter
        // ====================================================

        List<Map<String, Object>> configs =
                Arrays.asList(
                        searchConfig,
                        uploadConfig
                );


        RateLimiter rateLimiter =
                new RateLimiter(
                        configs,
                        defaultConfig
                );

        
        // ====================================================
        // TEST 1: Token Bucket
        // /search
        // ====================================================

        System.out.println(
                "========== TOKEN BUCKET TEST =========="
        );


        String client1 = "user1";


        for (int i = 1; i <= 5; i++) {

            RateLimitResult result =
                    rateLimiter.allow(
                            client1,
                            "/search"
                    );


            System.out.println(
                    "Request " + i +
                    " -> " + result
            );
        }

        // ====================================================
        // Wait for one token to refill
        // ====================================================

        System.out.println(
                "\nWaiting 1 second..."
        );


        Thread.sleep(1000);


        RateLimitResult resultAfterWait =
                rateLimiter.allow(
                        client1,
                        "/search"
                );


        System.out.println(
                "After 1 second -> "
                        + resultAfterWait
        );


        // ====================================================
        // TEST 2: Different client
        // ====================================================

        System.out.println(
                "\n========== DIFFERENT CLIENT =========="
        );


        RateLimitResult user2Result =
                rateLimiter.allow(
                        "user2",
                        "/search"
                );


        System.out.println(
                "user2 -> " + user2Result
        );


        // ====================================================
        // TEST 3: Sliding Window
        // /upload
        //
        // max = 2 requests / 5 seconds
        // ====================================================

        System.out.println(
                "\n========== SLIDING WINDOW TEST =========="
        );


        String client3 = "user3";


        for (int i = 1; i <= 3; i++) {

            RateLimitResult result =
                    rateLimiter.allow(
                            client3,
                            "/upload"
                    );


            System.out.println(
                    "Upload request " + i +
                    " -> " + result
            );
        }


        // ====================================================
        // TEST 4: Unknown endpoint
        //
        // Should use default limiter
        // ====================================================

        System.out.println(
                "\n========== DEFAULT LIMITER TEST =========="
        );


        for (int i = 1; i <= 3; i++) {

            RateLimitResult result =
                    rateLimiter.allow(
                            "user4",
                            "/unknown"
                    );


            System.out.println(
                    "Unknown endpoint request "
                            + i +
                            " -> " +
                            result
            );
        }


        // ====================================================
        // TEST 5: Concurrent requests
        // ====================================================

        System.out.println(
                "\n========== CONCURRENCY TEST =========="
        );


        String concurrentClient =
                "concurrent-user";


        Runnable task = () -> {

            RateLimitResult result =
                    rateLimiter.allow(
                            concurrentClient,
                            "/search"
                    );


            System.out.println(
                    Thread.currentThread().getName()
                            + " -> "
                            + result
            );
        };


        Thread t1 =
                new Thread(task, "Thread-1");

        Thread t2 =
                new Thread(task, "Thread-2");

        Thread t3 =
                new Thread(task, "Thread-3");

        Thread t4 =
                new Thread(task, "Thread-4");


        t1.start();
        t2.start();
        t3.start();
        t4.start();


        t1.join();
        t2.join();
        t3.join();
        t4.join();


        System.out.println(
                "\n========== DONE =========="
        );
    }
}
