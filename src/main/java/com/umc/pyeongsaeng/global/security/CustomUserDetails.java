package com.umc.pyeongsaeng.global.security;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.umc.pyeongsaeng.domain.user.entity.User;
import com.umc.pyeongsaeng.domain.user.enums.UserStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CustomUserDetails implements UserDetails {

	private final Long userId;
	private final String username;
	private final String password;
	private final String role;
	private final boolean enabled;
	private final User user;

	/**
	 * User 엔티티로부터 CustomUserDetails 생성
	 * @param user User 엔티티
	 * @return CustomUserDetails
	 */
	public static CustomUserDetails from(User user) {
		return CustomUserDetails.builder()
			.userId(user.getId())
			.username(user.getUsername())
			.password(user.getPassword() != null ? user.getPassword() : "")
			.role(user.getRole().name())
			.enabled(user.getStatus() == UserStatus.ACTIVE)
			.user(user)
			.build();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
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
		return enabled;
	}
}
