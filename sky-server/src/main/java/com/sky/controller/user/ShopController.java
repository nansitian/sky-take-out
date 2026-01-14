package com.sky.controller.user;

import com.sky.result.Result;
import com.sky.service.ShopService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("userShopController")
@Slf4j
@RequestMapping("/user/shop")
@Api(tags = "店铺相关接口")
public class ShopController {
    @Autowired
    ShopService shopService;

    @GetMapping("/status")
    @ApiOperation("查询店铺营业状态")
    public Result<Integer> getShopStatus(){
        Integer status = shopService.getShopStatus();
        log.info("查询店铺营业状态 :{}",status == 1 ? "营业中" : "打样中" );
        return Result.success(status);
    }
}
