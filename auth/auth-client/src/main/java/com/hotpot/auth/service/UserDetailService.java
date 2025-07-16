package com.hotpot.auth.service;

import com.hotpot.common.entity.BaseUser;
import com.hotpot.common.entity.SysUser;
import com.hotpot.common.entity.UserInfo;
import com.hotpot.auth.feign.UserServiceApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * @author Peter
 * @date 2023/3/3
 * @description
 */
@Service
public class UserDetailService implements UserDetailsService {

    @Autowired
    private UserServiceApi userServiceApi;

    @Autowired
    private AuthorityService authorityService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserInfo userDetails = userServiceApi.getUserDetails(username);
        if (null == userDetails) {
            throw new UsernameNotFoundException("user not found");
        }
        return getUserDetails(userDetails);
    }

    private UserDetails getUserDetails(UserInfo info) {

        Set<GrantedAuthority> authorities = authorityService.reloadByUser(info);
        SysUser user = info.getSysUser();
        boolean enabled = true; // TODO
        boolean nonLockFlag = true; // TODO

        return new BaseUser(user.getId(), user.getUsername(), user.getPassword(), enabled, true, true, nonLockFlag, authorities);
    }

}
