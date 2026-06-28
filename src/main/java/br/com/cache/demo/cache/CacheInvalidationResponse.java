package br.com.cache.demo.cache;

public record CacheInvalidationResponse(
        String key,
        boolean removed
) {
}
