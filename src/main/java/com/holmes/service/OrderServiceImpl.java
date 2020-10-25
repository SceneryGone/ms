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
@Transactional(rollbackFor = Exception.class)
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
    @Override
    public Integer kill(Integer stockId) {
        // 1. 判断库存
        Stock stock = checkStock(stockId);

        // 2. 扣库存
        int subStock = subStock(stock);
        if (subStock <= 0) {
            log.error("没有抢到商品");
            throw new BizException("没有抢到该商品");
        }

        // 3. 创建订单
        return createStockOrder(stockId, stock);
    }

    /**
     * 创建订单
     */
    private Integer createStockOrder(Integer stockId, Stock stock) {
        log.info("创建库存:{}", stockId);
        StockOrder stockOrder = new StockOrder();
        stockOrder.setSid(stock.getId());
        stockOrder.setName(stock.getName());
        stockOrderMapper.insertSelective(stockOrder);
        return stockOrder.getId();
    }

    /**
     * 扣库存
     */
    private int subStock(Stock stock) {
        log.info("扣除库存:{}", stock.getId());
        return stockMapper.subStackSale(stock);
    }

    /**
     * 判断库存
     */
    private Stock checkStock(Integer stockId) {
        Stock stock = stockMapper.selectByPrimaryKey(stockId);
        if (stock.getCount().equals(stock.getSale())) {
            throw new BizException("商品被卖光啦!");
        }
        return stock;
    }

}
