package com.example.ReportManagement.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

public class LoginUser implements UserDetails {

	private static final long serialVersionUID = -8548497120810440086L;
	private int user_id;
	private String user_name;
	private String password;
	private List<GrantedAuthority> authorities = new ArrayList<>();

	public LoginUser(int user_id, String user_name, String password, String authority) {
		this.user_id = user_id;
		this.user_name = user_name;
		this.password = password;
		this.authorities = AuthorityUtils.createAuthorityList(authority);

	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return user_name;
	}

	public int getUserId() {
		return user_id;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}