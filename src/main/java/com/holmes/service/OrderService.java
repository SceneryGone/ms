package com.holmes.service;

public interface OrderService {

    /**
     * @description: 秒杀下单方法
     * @param stockId 商品id
     * @return {@link Integer} 返回的订单id
     * @author: holmes
     * @date: 2020/10/25 3:47 下午
     */
    Integer kill(Integer stockId);
}
