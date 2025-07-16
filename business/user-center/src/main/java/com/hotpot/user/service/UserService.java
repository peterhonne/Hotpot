package com.hotpot.user.service;

import com.google.common.collect.Lists;
import com.hotpot.common.dto.SysUserDTO;
import com.hotpot.common.entity.SysUser;
import com.hotpot.common.entity.UserInfo;
import com.hotpot.user.dao.SysUserMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Peter
 * @date 2023/3/6
 * @description
 */
@Service
public class UserService {
    private static final PasswordEncoder PASSWORD_ENCODER = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    @Autowired
    private SysUserMapper sysUserMapper;

    public UserInfo getInfoByUsername(String username) {
        UserInfo userInfo = new UserInfo();
        SysUser user = sysUserMapper.getByUsername(username);
        if (null == user) {
            return null;
        }
        userInfo.setSysUser(user);
        userInfo.setRoles(new String[]{"admin"});
        userInfo.setAuthorities(new String[]{"user:add"});
        userInfo.setAuthorities(new String[]{"user:select"});
        return userInfo;
    }


    public void save(SysUserDTO sysUserDTO) {
        SysUser sysUser = new SysUser();
        BeanUtils.copyProperties(sysUserDTO, sysUser);
        sysUserMapper.save(sysUser);
    }

    public Boolean deleteUser(String username) {
        SysUser user = sysUserMapper.getByUsername(username);
        if (null != user) {
            sysUserMapper.deleteById(user.getId());
        }
        return true;
    }
}
