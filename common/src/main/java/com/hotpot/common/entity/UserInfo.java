package com.hotpot.common.entity;



import lombok.Data;

import java.io.Serializable;


@Data
public class UserInfo implements Serializable {

    private SysUser sysUser;

    private String[] authorities;

    private String[] roles;
}
