package com.hotpot.common.entity;


import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Where;

import java.util.List;

@Data
@Entity
@Table(name = "sys_user")
@Where(clause = "del_flag = 0")
public class SysUser {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String username;

    private String name;

    private String email;

    protected String password;

    private String salt;

    private Boolean delFlag;

    private Boolean lockFlag;

    private String phone;

    private String avatar;

    private String wxOpenid;

    private String qqOpenid;
    @Transient
    private List<String> role;
}
