package com.sky.service;

public interface ShopService {

    /**
     * 设置店铺营业状态
     * @param status
     */
    void setShopStatus(Integer status);

    /**
     * 查询店铺营业状态
     * @return
     */
    Integer getShopStatus();
}
