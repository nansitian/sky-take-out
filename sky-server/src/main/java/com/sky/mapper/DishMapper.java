package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.SetmealDish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishMapper {

    /**
     * 插入菜品数据
     * @param dish
     */
    @AutoFill(OperationType.INSERT)
    void insert(Dish dish);

    /**
     * 菜品分页查询
     * @param dto
     * @return
     */
    List<DishVO> pageQuery(DishPageQueryDTO dto);

    /**
     * 根据主键id查询菜品
     * @param id
     * @return
     */
    @Select("select * from dish where id = #{id}")
    Dish getById(Long id);

    /**
     * 根据主键id删除菜品数据
     * @param id
     */
    @Delete("delete from dish where id = #{id}")
    void deleteById(Long id);

    /**
     * 根据菜品id集合批量删除菜品数据
     * @param ids
     */
    void deleteByIds(List<Long> ids);

    /**
     * 根据id修改菜基本信息
     * @param dish
     */
    @AutoFill(OperationType.UPDATE)
    void update(Dish dish);

    /**
     * 根据分类id查询菜品信息
     * @param dish
     * @return
     */
    List<Dish> getByCategoryId(Dish dish);

    /**
     * 根据套餐id查询菜品数据
     * @param setmealId
     * @return
     */
    @Select("select * from dish d left join setmeal_dish sd on d.id = sd.dish_id and sd.setmeal_id = #{setmealId}")
    List<Dish> getBySetmealId(Long setmealId);
}
