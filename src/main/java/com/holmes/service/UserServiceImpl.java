package com.holmes.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author: holmes
 * @date: 2020/10/28 11:19 上午
 */
@Slf4j
@Component
public class UserServiceImpl implements UserService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * @description: 保存用户访问次数
     * @param userId 用户id
     * @return {@link int}
     * @author: holmes
     * @date: 2020/10/28 11:29 上午
     */
    @Override
    public int saveUserCount(Integer userId) {
        String limitKey = "LIMIT_" + userId;
        String limitNumber = stringRedisTemplate.opsForValue().get(limitKey);
        int limit = 0;
        if (limitNumber == null || Integer.parseInt(limitNumber) < 0) {
            stringRedisTemplate.opsForValue().set(limitKey, "0", 60, TimeUnit.SECONDS);
        } else {
            limit = Integer.parseInt(limitNumber) + 1;
            stringRedisTemplate.opsForValue().set(limitKey, String.valueOf(limit), 60, TimeUnit.SECONDS);
        }
        return limit;
    }

    /**
     * @description: 判断调用次数是否超时
     * @param userId 用户id
     * @return {@link boolean} true 超时
     * @author: holmes
     * @date: 2020/10/28 11:29 上午
     */
    @Override
    public boolean getUserCount(Integer userId) {
        String limitKey = "LIMIT_" + userId;
        String limitNumber = stringRedisTemplate.opsForValue().get(limitKey);
        if (limitNumber == null) {
            log.info("用户调用异常");
            return true;
        }
        return Integer.parseInt(limitNumber) > 10;
    }
}
