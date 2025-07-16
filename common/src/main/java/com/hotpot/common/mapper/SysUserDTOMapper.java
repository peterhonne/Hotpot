package com.hotpot.common.mapper;

import com.hotpot.common.dto.SysUserDTO;
import com.hotpot.common.entity.SysUser;

import java.util.function.Function;

/**
 * @author Peter
 * @date 2023/3/9
 * @description
 */
public class SysUserDTOMapper implements Function<SysUser, SysUserDTO> {
    @Override
    public SysUserDTO apply(SysUser sysUser) {
        return null;
    }

    @Override
    public <V> Function<V, SysUserDTO> compose(Function<? super V, ? extends SysUser> before) {
        return Function.super.compose(before);
    }
}
