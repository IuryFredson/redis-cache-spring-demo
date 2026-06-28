package br.com.cache.demo.cache;

import br.com.cache.demo.product.ProductResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
public class ProductCacheService {

    private static final String KEY_PREFIX = "product:";

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final Duration ttl;

    public ProductCacheService(
            StringRedisTemplate redisTemplate,
            ObjectMapper objectMapper,
            @Value("${app.cache.product-ttl-seconds}") long ttlSeconds
    ) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.ttl = Duration.ofSeconds(ttlSeconds);
    }

    public Optional<ProductResponse> get(Long id) {
        String json = redisTemplate.opsForValue().get(key(id));
        if (json == null) {
            return Optional.empty();
        }

        try {
            return Optional.of(objectMapper.readValue(json, ProductResponse.class));
        } catch (JsonProcessingException exception) {
            evict(id);
            return Optional.empty();
        }
    }

    public void put(Long id, ProductResponse response) {
        try {
            redisTemplate.opsForValue().set(key(id), objectMapper.writeValueAsString(response), ttl);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Nao foi possivel serializar o produto para cache", exception);
        }
    }

    public boolean evict(Long id) {
        return Boolean.TRUE.equals(redisTemplate.delete(key(id)));
    }

    public long ttlSeconds(Long id) {
        Long seconds = redisTemplate.getExpire(key(id));
        return seconds == null ? -2 : seconds;
    }

    public String key(Long id) {
        return KEY_PREFIX + id;
    }
}
