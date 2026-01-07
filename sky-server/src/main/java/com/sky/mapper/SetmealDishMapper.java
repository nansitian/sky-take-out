package com.sky.mapper;

import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    /**
     * 根据菜品id查询对应的套餐id
     * @param dishIds
     * @return
     */
    //select setmeal_id from setmeal_dish where dish_id in (1,2,3)
    List<Long> getSetmealIdByDishId(List<Long> dishIds);


    /**
     * 插入套餐id菜品id对应关系
     * @param setmealDish
     */
    void insert(SetmealDish setmealDish);

    /**
     * 根据套餐id集合删除对应套餐菜品关系
     * @param setmealIds
     */
    void deleteBySetmealIds(List<Long> setmealIds);

    /**
     * 根据套餐id删除对应套餐菜品关系
     * @param setmealId
     */
    @Delete("delete from setmeal_dish where setmeal_id = #{setmealId}")
    void deleteBySetmealId(Long setmealId);

    /**
     * 根据套餐id查询套餐菜品对应关系
     * @param setmealId
     * @return
     */
    @Select("select id, setmeal_id, dish_id, name, price, copies from setmeal_dish where setmeal_id = #{setmealId}")
    List<SetmealDish> getByDishId(Long setmealId);


}
