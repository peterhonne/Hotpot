package com.hotpot.common.utils;


import com.hotpot.common.constant.SecurityConstant;
import com.hotpot.common.entity.BaseUser;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.PatternMatchUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Peter
 * @date 2023/3/06
 * @description
 */
@UtilityClass
public class SecurityUtils {

    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }


    public BaseUser getUser(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof BaseUser) {
            return (BaseUser) principal;
        }
        return null;
    }


    public BaseUser getUser() {
        Authentication authentication = getAuthentication();
        return getUser(authentication);
    }

    public Integer getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
        BaseUser user = SecurityUtils.getUser(authentication);

        return user != null ? user.getId() : null;

    }

    public String getUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
        return authentication.getName();
    }


    public boolean hasRole(String role) {
        if (StringUtils.isBlank(role)) {
            return false;
        }
        return hasPermission(SecurityConstant.ROLE_PREFIX + role);
    }


    public boolean hasPermission(String permission) {
        if (StringUtils.isBlank(permission)) {
            return false;
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .filter(StringUtils::isNotBlank)
                .anyMatch(x -> PatternMatchUtils.simpleMatch(permission, x));
    }

}
