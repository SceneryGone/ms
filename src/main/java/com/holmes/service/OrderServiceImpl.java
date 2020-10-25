package com.holmes.service;

import com.holmes.dao.StockMapper;
import com.holmes.dao.StockOrderMapper;
import com.holmes.entity.Stock;
import com.holmes.entity.StockOrder;
import com.holmes.util.BizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author: holmes
 * @date: 2020/10/25 3:48 下午
 */
@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    @Resource
    private StockMapper stockMapper;

    @Resource
    private StockOrderMapper stockOrderMapper;

    /**
     * @description: 秒杀下单方法
     * @param stockId 商品id
     * @return {@link Integer} 返回的订单id
     * @author: holmes
     * @date: 2020/10/25 3:47 下午
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Integer kill(Integer stockId) {
        // 判断库存
        log.info("库存判断:{}", stockId);
        Stock stock = stockMapper.selectByPrimaryKey(stockId);
        if (stock.getCount().equals(stock.getSale())) {
            throw new BizException("商品被卖光啦!");
        }

        // 扣库存
        log.info("扣除库存:{}", stockId);
        stock.setSale(stock.getSale() + 1);
        stockMapper.updateByPrimaryKey(stock);

        // 创建订单
        log.info("创建库存:{}", stockId);
        StockOrder stockOrder = new StockOrder();
        stockOrder.setSid(stock.getId());
        stockOrder.setName(stock.getName());
        stockOrderMapper.insertSelective(stockOrder);

        // 返回订单id
        Integer orderId = stockOrder.getId();
        log.info("返回订单ID:{}", orderId);
        return orderId;
    }

}
