package com.sky.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(description = "新增员工时传递的数据模型")
public class EmployeeDTO implements Serializable {//1.获得化作流的资格, 可以存如redis  2.对象被写到硬盘里面持久化 3.会有版本锁定功能"版本指纹"

    @ApiModelProperty("主键ID")
    private Long id;

    @ApiModelProperty("账号")
    private String username;

    @ApiModelProperty("员工姓名")
    private String name;

    @ApiModelProperty("手机号")
    private String phone;

    @ApiModelProperty("性别")
    private String sex;

    @ApiModelProperty("身份证号")
    private String idNumber;

}
