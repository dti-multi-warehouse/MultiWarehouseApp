package com.dti.multiwarehouse.auth.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;
@Repository
public class AuthRedisRepository {
    private static final String STRING_KEY_PREFIX = "AlphaMarch:jwt:strings:" ;
    private static final String STRING_BLACKLIST_KEY_PREFIX = "AlphaMarch:blacklist-jwt:strings:" ;
    private final ValueOperations<String, String> valueOps;
    private static final Logger logger = LoggerFactory.getLogger(AuthRedisRepository.class);

    public AuthRedisRepository(RedisTemplate<String, String> redisTemplate) {
        this.valueOps = redisTemplate.opsForValue();
    }

    public void saveJwtKey(String email, String jwtKey) {
        valueOps.set(STRING_KEY_PREFIX + email, jwtKey, 1, TimeUnit.HOURS);
    }

    public String getJwtKey(String email) {
        try {
            String jwtKey = valueOps.get(STRING_KEY_PREFIX + email);
            return jwtKey;
        } catch (RedisConnectionFailureException ex) {
            logger.error("Failed to retrieve JWT key from Redis for email: {}. Connection failed.", email, ex);
        }
        return null;
    }

    public void deleteJwtKey(String email) {
        valueOps.getOperations().delete(STRING_KEY_PREFIX + email);
    }

    public void blackListJwt(String email, String jwt) {
        valueOps.set(STRING_BLACKLIST_KEY_PREFIX + jwt, email, 1, TimeUnit.HOURS);
    }

    public Boolean isKeyBlacklisted(String jwt) {
        try {
            boolean isBlacklisted = valueOps.get(STRING_BLACKLIST_KEY_PREFIX + jwt) != null;
            logger.debug("Checked blacklist status for JWT: {}. Blacklisted: {}", jwt, isBlacklisted);
            return isBlacklisted;
        } catch (RedisConnectionFailureException ex) {
            logger.error("Failed to check blacklist status for JWT: {}. Connection failed.", jwt, ex);
        }
        return false;
    }
}
