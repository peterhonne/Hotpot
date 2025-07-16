package com.hotpot.common.entity;

import lombok.Data;


/**
 * @author Peter
 * @date 2023/3/06
 * @description
 */
@Data
public class BaseVo<T> {

    private T data;

    public BaseVo(){}

    public BaseVo(T data){
        this.data = data;
    }
}
