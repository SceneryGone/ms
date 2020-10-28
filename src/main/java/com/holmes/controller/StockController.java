package com.holmes.controller;

import com.google.common.util.concurrent.RateLimiter;
import com.holmes.service.OrderService;
import com.holmes.service.UserService;
import com.holmes.util.BizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author: holmes
 * @date: 2020/10/25 3:44 下午
 */
@Slf4j
@RestController
@RequestMapping("/stock")
public class StockController {

    /**
     * 基于谷歌令牌桶算法 每秒接收100个请求
     */
    private static final RateLimiter RATE_LIMITER = RateLimiter.create(100);

    @Resource
    private OrderService orderService;

    @Resource
    private UserService userService;

    /**
     * @description: 秒杀
     * @param stockId 商品id
     * @return {@link String}
     * @author: holmes
     * @date: 2020/10/25 3:44 下午
     */
    @GetMapping("/kill")
    public String kill(Integer stockId, Integer userId, String md5) {
        try {
            // 限流
            if (!RATE_LIMITER.tryAcquire(1, TimeUnit.SECONDS)) {
                throw new BizException("活动太火爆啦~等会再试!");
            }

            // 限制单个用户调用频率
            int count = userService.saveUserCount(userId);
            log.info("用户:{} 访问次数为:{}", userId, count);
            boolean isBanned = userService.getUserCount(userId);
            if (isBanned) {
                return "购买失败,超过频率限制";
            }

            // 调用秒杀业务
            Integer orderId = orderService.kill(stockId, userId, md5);
            return "秒杀成功,订单id:" + orderId;
        } catch (BizException e) {
            log.info("exception:{}", e.getMessage());
            return e.getMessage();
        }
    }

    /**
     * @description: 获取md5
     * @param stockId 商品id
     * @param userId 用户id
     * @return {@link String}
     * @author: holmes
     * @date: 2020/10/27 10:14 下午
     */
    @GetMapping(value = "/md5")
    public String md5(Integer stockId, Integer userId) {
        try {
            return orderService.md5(stockId, userId);
        } catch (BizException e) {
            log.info("exception:{}", e.getMessage());
            return e.getMessage();
        }
    }

}
