package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {

    /**
     * 新增菜品和对应的口味数据
     * @param dto
     */
    public void saveWithFlavor(DishDTO dto);

    /**
     * 菜品分页查询
     * @param dto
     * @return
     */
    PageResult pageQuery(DishPageQueryDTO dto);


    /**
     * 批量删除菜品
     * @param ids
     */
    void deleteBatch(List<Long> ids);

    /**
     * 根据id查询菜品数据
     * @param id
     */
    DishVO getByIdWithFlavor(Long id);

    /**
     * 根据id修改菜品的基本信息和对应的口味信息
     * @param dto
     */
    void updateWithFlavor(DishDTO dto);

    /**
     * 菜品起售停售
     * @param id
     * @param status
     */
    void startOrStop(Long id ,Integer status);

    /**
     * 根据分类id查询菜品
     * @param id
     * @return
     */
    List<Dish> getByCategoryId(Long id);
}
