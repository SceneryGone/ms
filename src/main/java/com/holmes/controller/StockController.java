package com.holmes.controller;

import com.google.common.util.concurrent.RateLimiter;
import com.holmes.service.OrderService;
import com.holmes.util.BizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: holmes
 * @date: 2020/10/25 3:44 下午
 */
@Slf4j
@RestController
@RequestMapping("/stock")
public class StockController {

    private static AtomicInteger COUNT = new AtomicInteger(0);

    /**
     * 基于谷歌令牌桶算法 每秒接收100个请求
     */
    private static final RateLimiter RATE_LIMITER = RateLimiter.create(100);

    @Resource
    private OrderService orderService;

    /**
     * @description: 秒杀
     * @param stockId 商品id
     * @return {@link String}
     * @author: holmes
     * @date: 2020/10/25 3:44 下午
     */
    @GetMapping("/kill")
    public String kill(Integer stockId) {
        log.info("请求次数:{}", COUNT.incrementAndGet());
        try {
            if (!RATE_LIMITER.tryAcquire(2, TimeUnit.SECONDS)) {
                throw new BizException("活动太火爆啦~等会再试!");
            }

            Integer orderId = orderService.kill(stockId);
            return "秒杀成功,订单id:" + orderId;
        } catch (BizException e) {
            log.info("exception:{}", e.getMessage());
            return e.getMessage();
        }
    }

}
