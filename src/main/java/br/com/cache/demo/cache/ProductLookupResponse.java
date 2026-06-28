package br.com.cache.demo.cache;

import br.com.cache.demo.product.ProductResponse;

public record ProductLookupResponse(
        CacheSource source,
        double durationMs,
        long ttlSeconds,
        ProductResponse data
) {
}
