package com.holmes.service;

public interface OrderService {

    /**
     * @description: 秒杀下单方法
     * @param stockId
     * @param userId
     * @param md5
     * @return {@link Integer} 返回的订单id
     * @author: holmes
     * @date: 2020/10/25 3:47 下午
     */
    Integer kill(Integer stockId, Integer userId, String md5);

    /**
     * @description: 获取md5
     * @param stockId 商品id
     * @param userId 用户id
     * @return {@link String}
     * @author: holmes
     * @date: 2020/10/27 10:14 下午
     */
    String md5(Integer stockId, Integer userId);
}
