package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.exception.SetmealEnableFailedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class SetmealServiceImpl implements SetmealService {
    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private DishMapper dishMapper;

    /**
     * 新增套餐
     * @param setmealDTO
     */
    @Override
    @Transactional
    public void insert(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        //插入套餐基本信息
        setmealMapper.insert(setmeal);

        //插入套餐与菜品对应关系
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        for (SetmealDish setmealDish : setmealDishes) {
            //获取套餐id并存入对应关系中
            setmealDish.setSetmealId(setmeal.getId());
            //将每种对应关系存入套餐与菜品对应关系表中
            setmealDishMapper.insert(setmealDish);
        }
    }

    /**
     * 分页查询
     * @param setmealPageQueryDTO
     * @return
     */
    @Override
    public PageResult page(SetmealPageQueryDTO setmealPageQueryDTO) {
        //开始分页查询
        PageHelper.startPage(setmealPageQueryDTO.getPage(),setmealPageQueryDTO.getPageSize());
        Page<Setmeal> page = (Page<Setmeal>) setmealMapper.select(setmealPageQueryDTO);
        return new PageResult(page.getTotal(),page.getResult());
    }

    /**
     * 批量删除套餐
     * @param ids
     */
    @Override
    @Transactional
    public void delete(List<Long> ids) {
        for (Long id : ids) {
            Setmeal setmeal = setmealMapper.selectById(id);
            if(setmeal.getStatus() == StatusConstant.ENABLE){
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
        //删除套餐基本信息
        setmealMapper.delete(ids);
        //删除该套餐对应的套餐菜品对应关系
        setmealDishMapper.deleteBySetmealIds(ids);

    }

    /**
     * 修改套餐
     * @param setmealVO
     */
    @Override
    @Transactional
    public void update(SetmealVO setmealVO) {
        //修改套餐基本信息
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealVO,setmeal);
        setmealMapper.update(setmeal);
        //修改套餐菜品对应关系
        //先删除原本的关系, 再插入新关系
        setmealDishMapper.deleteBySetmealId(setmeal.getId());
        List<SetmealDish> setmealDishes = setmealVO.getSetmealDishes();
        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(setmeal.getId());
            setmealDishMapper.insert(setmealDish);
        }
    }

    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    @Override
    public SetmealVO getById(Long id) {
        //根据套餐id, 查询套餐基本数据
        Setmeal setmeal = setmealMapper.selectById(id);
        //根据套餐id, 查询套餐菜品对应关系
        List<SetmealDish> list = setmealDishMapper.getByDishId(id);
        //将两条查询数组插入到setmealvo 对象并返回
        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal,setmealVO);
        setmealVO.setSetmealDishes(list);
        return setmealVO;
    }

    @Override
    public void startOrstop(Integer status, Long id) {
        //起售套餐时，如果套餐内包含停售的菜品，则不能起售
        if(status == StatusConstant.ENABLE){
            //起售套餐时, 判断套餐内菜品是否为停售
            //根据套餐id查询菜品数据
            List<Dish> setmealDishes = dishMapper.getBySetmealId(id);
            if(setmealDishes != null && setmealDishes.size() > 0){
                for (Dish setmealDish : setmealDishes) {
                    if(setmealDish.getStatus() == StatusConstant.DISABLE){
                        //菜品处于禁售
                        throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
                    }
                }
            }
        }
        Setmeal setmeal = new Setmeal();
        setmeal.setId(id);
        setmeal.setStatus(status);
        setmealMapper.update(setmeal);
    }

    /**
     * 条件查询
     * @param setmeal
     * @return
     */
    public List<Setmeal> list(Setmeal setmeal) {
        List<Setmeal> list = setmealMapper.list(setmeal);
        return list;
    }

    /**
     * 根据id查询菜品选项
     * @param id
     * @return
     */
    public List<DishItemVO> getDishItemById(Long id) {
        return setmealMapper.getDishItemBySetmealId(id);
    }
}
