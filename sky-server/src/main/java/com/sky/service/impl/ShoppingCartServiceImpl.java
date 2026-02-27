package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    @Autowired
    ShoppingCartMapper shoppingCartMapper;
    @Autowired
    DishMapper dishMapper;
    @Autowired
    SetmealMapper setmealMapper;


    /**
     * 添加购物车
     * @param shoppingCartDTO
     */
    @Override
    public void addShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        //判断当前插入购物车的商品是否已经存在了
        ShoppingCart shoppingCart = new ShoppingCart();
        //在查询条件中添加上用户id
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);

        //如果存在, 商品的数量加1
        if(list != null && list.size() > 0){
            ShoppingCart sc = list.get(0);
            //调用方法, 根据id更新数量的数据+1
            sc.setNumber(sc.getNumber() + 1);
            shoppingCartMapper.updateNumberById(sc);
        }else {
            //如果不存在, 添加商品到购物车当中
            //判断当前要插入的是菜品还是套餐
            Long dishId = shoppingCart.getDishId();
            if(dishId != null){
                //插入的是菜品
                //根据菜品id查询菜品数据
                Dish dish = dishMapper.getById(dishId);
                shoppingCart.setName(dish.getName());
                shoppingCart.setAmount(dish.getPrice());
                shoppingCart.setImage(dish.getImage());
            }else {
                Long setmealId = shoppingCart.getSetmealId();
                Setmeal setmeal = setmealMapper.selectById(setmealId);
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setAmount(setmeal.getPrice());
                shoppingCart.setImage(setmeal.getImage());
            }
            //添加一些公共的属性, number creatTime
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            //将购物车的商品数据插入到数据库中
            shoppingCartMapper.insert(shoppingCart);
        }



    }

    /**
     * 查询购物车
     * @return
     */
    @Override
    public List<ShoppingCart> showShoppingCart() {
        //获取当前用户id
        Long userId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = ShoppingCart.builder().userId(userId).build();
        //查询购物车数据
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        return list;
    }

    /**
     * 清空购物车
     */
    @Override
    public void cleanShoppingCart() {
        //删除掉当前用户的购物车
        Long userId = BaseContext.getCurrentId();
        shoppingCartMapper.deleteByUserId(userId);

    }

    /**
     * 减少一个购物车商品
     * @param shoppingCartDTO
     */
    @Override
    public void subShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        //判断这个商品的数量是否为1
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        //查询条件中添加用户id
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        shoppingCart = list.get(0);
        if(shoppingCart.getNumber() == 1){
            //商品数量为1, 删除商品
            shoppingCartMapper.deleteById(shoppingCart);
            return;
        }
        //商品数量为多个, 修改商品的数量, 减一
        shoppingCart.setNumber(shoppingCart.getNumber() - 1);
        shoppingCartMapper.updateNumberById(shoppingCart);

    }
}
