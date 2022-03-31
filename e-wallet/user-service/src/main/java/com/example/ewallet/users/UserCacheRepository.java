package com.example.ewallet.users;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import javax.jws.soap.SOAPBinding;

@Repository
public class UserCacheRepository {


    private static final String KEY_PREFIX = "User::";
    private final RedisTemplate<String, Object> redisTemplate;

//    constructor injection
    public UserCacheRepository(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate=redisTemplate;
    }

    public void save(User user) {

        redisTemplate.opsForValue().set(KEY_PREFIX+user.getId(), user);
    }

    public User get(int userId) {
        return (User) redisTemplate.opsForValue().get(KEY_PREFIX+userId);
    }
}
