// Online Java Compiler (Editor)
// Write and run Java online using this editor.
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

class TokenBucketAlgorithm implements Limiter{
    private final int capacity;
    private final int refillRatePerSecond;
    private final Map<String, TokenBucket> buckets = new HashMap<>();

    public TokenBucketLimiter(int capacity, int refillRatePerSecond) {
        this.capacity = capacity;
        this.refillRatePerSecond = refillRatePerSecond;
    }
    
    @Override
    public RateLimitResult allow(String key){
        //Extracting the buckey from key
        Token bucket = buckets.computeIfAbsent(key,k-> new TokenBucket(capacity,System.currentTimeMillis()));
        long now = System.currentTimeMillis();
        //calculate amount of token generated during now and request time
        long elapsed = now-bucket.lastRefillTime
        //compute tokens to add based on time elasped
        double tokensToAdd = (elapsed * refillRatePerSecond) / 1000.0;//in secs
        bucket.tokens=Math.min(capacity,bucket.tokens+tokensToAdd);
        bucket.lastRefillTime = now;
        if(bucket.tokens>=1){
            //we have available tokens to process 1 request after refill
            bucket.tokens -= 1;
            int remaining = (int) Math.floor(bucket.tokens);
            return RateLimitResult(true,remaining,null);
        }
        
        //we don't have enough tokens
        //calculate retryAfterMS now and then return false
        double tokensNeeded = 1 - bucket.tokens;
        long retryAfterMs = (long) Math.ceil((tokensNeeded * 1000) / refillRatePerSecond);
        return new RateLimitResult(false, 0, retryAfterMs);
    }

    private static class TokenBucket{
        double tokens;
        long lastRefillTime;

        TokenBucket(double initialTokens, long time) {
            this.tokens = initialTokens;
            this.lastRefillTime = time;
        }
    }
}
class SlidingWindowLogLimiter implements Limiter{
    public SlidingWindowLogLimiter(){
        
    }
    
    @Override
    public RateLimitResult allow(String key){
        
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
            String endpoint = (String) config.get(endpoint);
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
    public static void main(String[] args) {
        
    }
}
