package br.com.cache.demo.product;

import br.com.cache.demo.cache.CacheSource;
import br.com.cache.demo.cache.ProductCacheService;
import br.com.cache.demo.cache.ProductLookupResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductCacheService productCacheService;
    private final long databaseDelayMs;

    public ProductService(
            ProductRepository productRepository,
            ProductCacheService productCacheService,
            @Value("${app.demo.database-delay-ms}") long databaseDelayMs
    ) {
        this.productRepository = productRepository;
        this.productCacheService = productCacheService;
        this.databaseDelayMs = databaseDelayMs;
    }

    @Transactional(readOnly = true)
    public ProductLookupResponse findById(Long id) {
        long start = System.nanoTime();

        return productCacheService.get(id)
                .map(product -> response(CacheSource.CACHE, start, productCacheService.ttlSeconds(id), product))
                .orElseGet(() -> {
                    ProductResponse product = findByIdFromDatabase(id);
                    productCacheService.put(id, product);
                    return response(CacheSource.DATABASE, start, productCacheService.ttlSeconds(id), product);
                });
    }

    @Transactional(readOnly = true)
    public ProductLookupResponse findByIdWithoutCache(Long id) {
        long start = System.nanoTime();
        ProductResponse product = findByIdFromDatabase(id);
        return response(CacheSource.DATABASE, start, -1, product);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> findAll() {
        return productRepository.findAll()
                .stream()
                .map(ProductResponse::from)
                .toList();
    }

    @Transactional
    public ProductResponse update(Long id, ProductUpdateRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        product.update(request);
        ProductResponse response = ProductResponse.from(product);
        productCacheService.evict(id);
        return response;
    }

    private ProductResponse findByIdFromDatabase(Long id) {
        applyDatabaseDelay();
        return productRepository.findById(id)
                .map(ProductResponse::from)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    private void applyDatabaseDelay() {
        if (databaseDelayMs <= 0) {
            return;
        }

        try {
            TimeUnit.MILLISECONDS.sleep(databaseDelayMs);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
    }

    private ProductLookupResponse response(CacheSource source, long start, long ttlSeconds, ProductResponse product) {
        double durationMs = (System.nanoTime() - start) / 1_000_000.0;
        return new ProductLookupResponse(source, durationMs, ttlSeconds, product);
    }
}
