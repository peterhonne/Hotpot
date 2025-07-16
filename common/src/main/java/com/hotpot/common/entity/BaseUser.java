package com.hotpot.common.entity;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * @author Peter
 * @date 2023/3/06
 * @description
 */
public class BaseUser extends User {

	@Getter
	private int id;

	@Getter
	private String organId;

	@Getter
	private String[] tenantIds;

	public BaseUser(int id, String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
		super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
		this.id = id;
		this.organId = organId;
		this.tenantIds = tenantIds;
	}
}
