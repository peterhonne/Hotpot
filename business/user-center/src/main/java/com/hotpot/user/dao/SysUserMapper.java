package com.hotpot.user.dao;


import com.hotpot.common.entity.SysUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SysUserMapper extends JpaRepository<SysUser, Integer> {

    SysUser getByUsername(String username);

}
