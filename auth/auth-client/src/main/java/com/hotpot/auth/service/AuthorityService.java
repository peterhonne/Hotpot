package com.hotpot.auth.service;

import com.google.common.collect.Lists;
import com.hotpot.common.constant.SecurityConstant;
import com.hotpot.common.entity.UserInfo;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Peter
 * @date 2023/3/8
 * @description
 */
@Service
public class AuthorityService {

    Set<GrantedAuthority> reloadByUser(UserInfo info) {
        if (null == info) {
            return Collections.unmodifiableSet(new LinkedHashSet<>(Lists.newArrayList(new SimpleGrantedAuthority(SecurityConstant.ROLE_NEW_USER))));
        }

        Set<String> dbAuthsSet = new HashSet<>();
        if (ArrayUtils.isNotEmpty(info.getRoles())) {
            // get role
            Arrays.stream(info.getRoles()).forEach(roleId -> dbAuthsSet.add(SecurityConstant.ROLE_PREFIX + roleId));
        }
        if (ArrayUtils.isNotEmpty(info.getAuthorities())) {
            // get resources
            dbAuthsSet.addAll(Arrays.asList(info.getAuthorities()));
        }
        return dbAuthsSet.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
    }


}
