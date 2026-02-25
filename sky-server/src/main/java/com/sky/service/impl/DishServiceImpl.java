package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.Setmeal;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 新增菜品和对应的口味数据
     * @param dto
     */
    @Override
    @Transactional  // 由于要同时处理两张表的内容, 所以为了保持数据一致性, 加上事务
    public void saveWithFlavor(DishDTO dto) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dto,dish);
        //向菜品表添加一条数据
        dishMapper.insert(dish);

        //获取insert语句生成的主键值
        Long dishId = dish.getId();

        List<DishFlavor> flavors = dto.getFlavors();
        if(flavors != null && flavors.size() > 0){
            flavors.forEach(flavor -> {
                flavor.setDishId(dishId);
            });
            //向口味表插入n条数据
            dishFlavorMapper.insertBatch(flavors);
        }


        }

    /**
     * 菜品分页查询
     * @param dto
     * @return
     */
    @Override
    public PageResult pageQuery(DishPageQueryDTO dto) {
        PageHelper.startPage(dto.getPage(),dto.getPageSize());
        Page<DishVO> page = (Page)dishMapper.pageQuery(dto);
        return new PageResult(page.getTotal(),page.getResult());
    }

    /**
     * 批量删除菜品
     * @param ids
     */
    @Override
    @Transactional
    public void deleteBatch(List<Long> ids) {
        //判断当前菜品是否能够被删除---是否存在起售中的菜品??
        for (Long id : ids) {
            Dish dish = dishMapper.getById(id);
            if(dish.getStatus() == StatusConstant.ENABLE){
                //当前菜品处于起售中, 不能删除
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
        //判断当前菜品是否能够被删除---是否被套餐关联
        List<Long> setmealIds = setmealDishMapper.getSetmealIdByDishId(ids);
        if(setmealIds != null && setmealIds.size() > 0){
            //当前菜品已经被套餐关联
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        //删除菜品表中菜品数据
        /*for (Long id : ids) {
            dishMapper.deleteById(id);
            //删除菜品关联的口味数据
            dishFlavorMapper.deleteByDishId(id);
        }*/

        //根据菜品id集合删除菜品数据
        dishMapper.deleteByIds(ids);

        //根据菜品id集合删除关联的口味数据
        dishFlavorMapper.deleteByDishIds(ids);
    }

    /**
     * 根据id查询菜品数据
     * @param id
     * @return
     */
    @Override
    public DishVO getByIdWithFlavor(Long id) {
        //根据id查询菜品表
        Dish dish = dishMapper.getById(id);
        //根据菜品id查询关联口味数据
        List<DishFlavor>  dishFlavors = dishFlavorMapper.getByDishId(id);
        //将查询到的数据封装到dishVo
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        dishVO.setFlavors(dishFlavors);
        return dishVO;
    }

    /**
     * 根据id修改菜品的基本信息和对应的口味信息
     * @param dto
     */
    @Override
    public void updateWithFlavor(DishDTO dto) {
        //修改菜品基本信息
        Dish dish = new Dish();
        BeanUtils.copyProperties(dto, dish);
        dishMapper.update(dish);
        //先删掉原来的口味数据
        dishFlavorMapper.deleteByDishId(dto.getId());
        //再重新插入新数据
        List<DishFlavor> flavors = dto.getFlavors();
        if(flavors != null && flavors.size() > 0){
            //插入dishId, 原本没有
            flavors.forEach(flavor ->{
                flavor.setDishId(dto.getId());
            });
            //向口味表中插入n条数据
            dishFlavorMapper.insertBatch(flavors);
        }


    }

    /**
     * 菜品起售停售
     * @param id
     * @param status
     */
    @Override
    public void startOrStop(Long id, Integer status) {
        Dish dish = new Dish();
        dish.setId(id);
        dish.setStatus(status);
        dishMapper.update(dish);
        //如果是停售操作的话, 那么当前套餐也要停售
        if(status == StatusConstant.DISABLE){
            List<Long> ids = new ArrayList<>();
            ids.add(id);
            //根据菜品id查询出来套餐的id
            List<Long> setmealIds = setmealDishMapper.getSetmealIdByDishId(ids);
            if(setmealIds != null && setmealIds.size() > 0){
                //当前菜品存在关联套餐, 需要将套餐停售
                for (Long setmealId : setmealIds) {
                    Setmeal setmeal = Setmeal.builder().id(setmealId).status(StatusConstant.DISABLE).build();
                    setmealMapper.update(setmeal);
                }

            }

        }
    }

    /**
     * 根据分类id查询菜品
     * @param id
     * @return
     */
    @Override
    public List<Dish> getByCategoryId(Long id) {
        Dish dish = new Dish();
        dish.setCategoryId(id);
        dish.setStatus(StatusConstant.ENABLE);//只返回启用的菜菜品, 未启用的菜品不会被查询到
        List<Dish> list = dishMapper.getByCategoryId(dish);
        return list;
    }

    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    public List<DishVO> listWithFlavor(Dish dish) {
        List<Dish> dishList = dishMapper.getByCategoryId(dish);

        List<DishVO> dishVOList = new ArrayList<>();

        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d,dishVO);

            //根据菜品id查询对应的口味
            List<DishFlavor> flavors = dishFlavorMapper.getByDishId(d.getId());

            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }

        return dishVOList;
    }


}
