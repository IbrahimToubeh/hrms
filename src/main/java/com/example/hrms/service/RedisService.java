package com.example.hrms.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;
    
    private static final String USER_STATUS_KEY_PREFIX = "UserStatus:";

    public String getUserStatus(String userId) {
        String key = USER_STATUS_KEY_PREFIX + userId;
        return redisTemplate.opsForValue().get(key);
    }
}
