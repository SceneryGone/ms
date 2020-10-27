package com.holmes.service;

import com.holmes.dao.StockMapper;
import com.holmes.dao.StockOrderMapper;
import com.holmes.dao.UserMapper;
import com.holmes.entity.Stock;
import com.holmes.entity.StockOrder;
import com.holmes.entity.User;
import com.holmes.util.BizException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author: holmes
 * @date: 2020/10/25 3:48 下午
 */
@Transactional(rollbackFor = Exception.class)
@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    private static final String STOCK_KEY_PREFIX = "kill_";


    @Resource
    private StockMapper stockMapper;

    @Resource
    private StockOrderMapper stockOrderMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private UserMapper userMapper;


    /**
     * @description: 秒杀下单方法
     * @param stockId
     * @param userId
     * @param md5
     * @return {@link Integer} 返回的订单id
     * @author: holmes
     * @date: 2020/10/25 3:47 下午
     */
    @Override
    public Integer kill(Integer stockId, Integer userId, String md5) {
        // 校验redis中的秒杀商品是否超时
        if (!stringRedisTemplate.hasKey(STOCK_KEY_PREFIX + stockId)) {
            throw new BizException("抢购活动已结束");
        }

        // 验证签名
        String hashKey = "KEY_" + userId + "_" + stockId;
        if (md5 == null || !StringUtils.equals(stringRedisTemplate.opsForValue().get(hashKey), md5)) {
            throw new BizException("当前请求数据不合法,请稍后再试");
        }

        // 1. 判断库存
        Stock stock = checkStock(stockId);

        // 2. 扣库存
        subStock(stock);

        // 3. 创建订单
        return createStockOrder(stockId, stock);
    }

    @Override
    public String md5(Integer stockId, Integer userId) {
        // 存在用户信息
        User user = userMapper.selectByPrimaryKey(userId);
        if (user == null) {
            throw new BizException("用户信息不存在");
        }

        // 存在商品信息
        Stock stock = stockMapper.selectByPrimaryKey(stockId);
        if (stock == null) {
            throw new BizException("商品信息不存在");
        }

        // 生成md5放入redis
        String hashKey = "KEY_" + userId + "_" + stockId;
        // 实际项目中随机生成即可
        String salt = "!@#$%";
        String value = DigestUtils.md5DigestAsHex((userId + stockId + salt).getBytes());
        stringRedisTemplate.opsForValue().set(hashKey, value, 20, TimeUnit.SECONDS);
        log.info("Redis写入的key:{},value:{}", hashKey, value);
        return value;
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
    private void subStock(Stock stock) {
        log.info("扣除库存:{}", stock.getId());
        int subStock = stockMapper.subStackSale(stock);
        if (subStock == 0) {
            throw new BizException("对不起,没有抢到该商品,请重试~");
        }
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
