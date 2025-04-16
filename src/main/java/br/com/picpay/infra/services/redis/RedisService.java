package br.com.picpay.infra.services.redis;

import br.com.picpay.common.utils.CacheUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;

@Log4j2
@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    public void save(String key, Object value, Duration duration) {
        try {
            redisTemplate.opsForValue().set(key, value, duration);
        } catch (Exception e) {
            log.error("Error saving key: {} value: {}", key, value, e);
        }
    }

    public <T>T get(String key, Class<T> clazz) {
       try {
            var cacheResult = redisTemplate.opsForValue().get(key);
            log.info("Cache result: {}", cacheResult);

            if (cacheResult != null)
                return clazz.cast(cacheResult);

            return null;
       } catch (Exception e) {
              log.error("Error getting key: {}", key, e);
              return null;
       }
    }

    public void delete(String key) {
        try {
            redisTemplate.delete(key);
        } catch (Exception e) {
            log.error("Error deleting key: {}", key, e);
        }
    }

    public void delete(Set<String> keys) {
        try {
            redisTemplate.delete(keys);
        } catch (Exception e) {
            log.error("Error deleting keys: {}", keys, e);
        }
    }

    public void deleteKeys(String... keys) {
        try {
            redisTemplate.delete(List.of(keys));
        } catch (Exception e) {
            log.error("Error deleting keys: {}", keys, e);
        }
    }

    public void deleteMatchingKeys(String transferPayerKey) {
        var matchingKeys = redisTemplate.keys(transferPayerKey);

        if (matchingKeys != null || !matchingKeys.isEmpty()) {
            redisTemplate.delete(matchingKeys);
            log.info("Deleted keys: {}", matchingKeys);
            return;
        }
        log.info("No keys found for pattern: {}", transferPayerKey);
    }

    public void deleteKeysByFilter(String filter) {
        try {
            redisTemplate.delete(Objects.requireNonNull(redisTemplate.keys(filter)));
        } catch (Exception e) {
            log.error("Error deleting keys by filter: {}", filter, e);
        }
    }

    public boolean existsByKey(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            log.error("Error checking if key exists: {}", key, e);
            return false;
        }
    }

    @Async
    public void evictCaches(List<Map.Entry<String, String>> entries) {
        Set<String> cacheKeys = new HashSet<>();

        entries.forEach(entry -> {
            String key = entry.getKey();
            String value = entry.getValue();
            if (key.contains("transfers") && !key.contains("amount")) {
                cacheKeys.add(CacheUtils.buildKeyFilter(key, value));
            } else {
                cacheKeys.add(CacheUtils.buildKey(key, value));
            }
        });

        cacheKeys.forEach((key) -> {
            if (key.contains("*")) {
                log.info("Deleting keys by filter: {}", key);
                deleteKeysByFilter(key);
            } else {
                log.info("Deleting key with no filter: {}", key);
                delete(key);
            }
        });

    }

}
