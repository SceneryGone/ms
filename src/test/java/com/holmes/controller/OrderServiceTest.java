package com.holmes.controller;


import com.holmes.MsApplicationTests;
import com.holmes.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import javax.annotation.Resource;

@Slf4j
public class OrderServiceTest extends MsApplicationTests {

    @Resource
    private OrderService orderService;

    @Test
    public void md5Test() {
        String md5 = orderService.md5(1, 1);
        log.info("md5:{}", md5);
    }
}
