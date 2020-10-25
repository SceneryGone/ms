package com.holmes.controller;

import com.holmes.service.OrderService;
import com.holmes.util.BizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: holmes
 * @date: 2020/10/25 3:44 下午
 */
@Slf4j
@RestController
@RequestMapping("/stock")
public class StockController {

    private static AtomicInteger count = new AtomicInteger(0);

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
        log.info("请求次数:{}", count.incrementAndGet());
        log.info("商品id:{}", stockId);
        Integer orderId = null;
        try {
            orderId = orderService.kill(stockId);
        } catch (BizException e) {
            log.info("exception:", e);
            return e.getMessage();
        }
        return "秒杀成功,订单id:" + orderId;
    }

}
