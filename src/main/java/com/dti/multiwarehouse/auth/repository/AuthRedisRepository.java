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
        try {
            valueOps.set(STRING_KEY_PREFIX + email, jwtKey, 1, TimeUnit.HOURS);
            logger.debug("Saved JWT key for email: {} with key: {}", email, jwtKey);
        } catch (RedisConnectionFailureException ex) {
            logger.error("Failed to save JWT key in Redis for email: {}. Connection failed.", email, ex);
        } catch (DataAccessException ex) {
            logger.error("Failed to save JWT key in Redis for email: {}. Data access exception.", email, ex);
        }
    }

    public String getJwtKey(String email) {
        try {
            String jwtKey = valueOps.get(STRING_KEY_PREFIX + email);
            logger.debug("Retrieved JWT key for email: {} with key: {}", email, jwtKey);
            return jwtKey;
        } catch (RedisConnectionFailureException ex) {
            logger.error("Failed to retrieve JWT key from Redis for email: {}. Connection failed.", email, ex);
        } catch (DataAccessException ex) {
            logger.error("Failed to retrieve JWT key from Redis for email: {}. Data access exception.", email, ex);
        }
        return null;
    }

    public void deleteJwtKey(String email) {
        try {
            valueOps.getOperations().delete(STRING_KEY_PREFIX + email);
            logger.debug("Deleted JWT key for email: {}", email);
        } catch (RedisConnectionFailureException ex) {
            logger.error("Failed to delete JWT key in Redis for email: {}. Connection failed.", email, ex);
        } catch (DataAccessException ex) {
            logger.error("Failed to delete JWT key in Redis for email: {}. Data access exception.", email, ex);
        }
    }

    public void blackListJwt(String email, String jwt) {
        try {
            valueOps.set(STRING_BLACKLIST_KEY_PREFIX + jwt, email, 1, TimeUnit.HOURS);
            logger.debug("Blacklisted JWT for email: {} with token: {}", email, jwt);
        } catch (RedisConnectionFailureException ex) {
            logger.error("Failed to blacklist JWT in Redis for email: {}. Connection failed.", email, ex);
        } catch (DataAccessException ex) {
            logger.error("Failed to blacklist JWT in Redis for email: {}. Data access exception.", email, ex);
        }
    }

    public Boolean isKeyBlacklisted(String jwt) {
        try {
            boolean isBlacklisted = valueOps.get(STRING_BLACKLIST_KEY_PREFIX + jwt) != null;
            logger.debug("Checked blacklist status for JWT: {}. Blacklisted: {}", jwt, isBlacklisted);
            return isBlacklisted;
        } catch (RedisConnectionFailureException ex) {
            logger.error("Failed to check blacklist status for JWT: {}. Connection failed.", jwt, ex);
        } catch (DataAccessException ex) {
            logger.error("Failed to check blacklist status for JWT: {}. Data access exception.", jwt, ex);
        }
        return false;
    }
}
