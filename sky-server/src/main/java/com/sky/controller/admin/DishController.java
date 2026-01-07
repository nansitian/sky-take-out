package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜品管理
 */
@RestController
@Slf4j
@Api(tags = "菜品管理")
@RequestMapping("/admin/dish")
public class DishController {
    @Autowired
    DishService dishService;

    /**
     * 新增菜品
     * @param dto
     * @return
     */
    @PostMapping
    @ApiOperation("新增菜品")
    public Result save(@RequestBody DishDTO dto){
        log.info("新增菜品: {}", dto);
        dishService.saveWithFlavor(dto);
        return Result.success();
    }


    /**
     * 菜品分页查询
     * @param dto
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("菜品分页查询")
    public Result<PageResult> page(DishPageQueryDTO dto){
        log.info("菜品分页查询: {}", dto);
        PageResult pageResult = dishService.pageQuery(dto);
        return Result.success(pageResult);
    }

    /**
     *批量删除菜品
     * @return
     */
    @DeleteMapping
    @ApiOperation("批量删除菜品")
    public Result delete(@RequestParam List<Long> ids){//@RequestParam 可以将接受到的字符串自动切割, 组成一个Long类型的集合
        log.info("批量删除菜品: {}", ids);
        dishService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 根据id查询菜品数据
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询菜品数据")
    public Result<DishVO> getById(@PathVariable Long id){//@PathVaruable 路径参数
        log.info("根据id查询菜品数据: {}", id);
        DishVO byIdWithFlavor = dishService.getByIdWithFlavor(id);
        return Result.success(byIdWithFlavor);
    }

    /**
     * 修改菜品
     * @param dto
     * @return
     */
    @PutMapping
    @ApiOperation("修改菜品")
    public Result update(@RequestBody DishDTO dto){
        dishService.updateWithFlavor(dto);
        return Result.success();
    }


    /**
     * 菜品起售停售
     * @param status
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("菜品起售停售")
    public Result startOrStop(Long id, @PathVariable Integer status){
        log.info("菜品起售停售: {}, {}",id, status);
        dishService.startOrStop(id, status);
        return Result.success();
    }

    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List> getByCategoryId(Long categoryId){
        log.info("根据分类id查询菜品:{}", categoryId);
        List<Dish> list = dishService.getByCategoryId(categoryId);
        return Result.success(list);
    }
}
