package com.sky.service.impl;

import com.sky.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class ShopServiceImpl implements ShopService {

    public static final String KEY = "SHOP_STATUS";

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 设置店铺营业状态
     * @param status
     */
    @Override
    public void setShopStatus(Integer status) {
        redisTemplate.opsForValue().set(KEY,status);
    }

    /**
     * 查询店铺营业状态
     * @return
     */
    @Override
    public Integer getShopStatus() {
        Integer shopStatus = (Integer) redisTemplate.opsForValue().get(KEY);
        return shopStatus;
    }
}
