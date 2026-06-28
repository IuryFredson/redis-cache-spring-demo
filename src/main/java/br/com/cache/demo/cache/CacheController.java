package br.com.cache.demo.cache;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cache/products")
public class CacheController {

    private final ProductCacheService productCacheService;

    public CacheController(ProductCacheService productCacheService) {
        this.productCacheService = productCacheService;
    }

    @DeleteMapping("/{id}")
    public CacheInvalidationResponse evictProduct(@PathVariable Long id) {
        return new CacheInvalidationResponse(productCacheService.key(id), productCacheService.evict(id));
    }
}
