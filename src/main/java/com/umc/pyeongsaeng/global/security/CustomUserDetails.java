package com.umc.pyeongsaeng.global.security;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.umc.pyeongsaeng.domain.company.entity.Company;
import com.umc.pyeongsaeng.domain.company.enums.CompanyStatus;
import com.umc.pyeongsaeng.domain.user.entity.User;
import com.umc.pyeongsaeng.domain.user.enums.UserStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CustomUserDetails implements UserDetails {

	private final Long id;
	private final String username;
	private final String password;
	private final String role;
	private final boolean enabled;
	private final User user;
	private final Company company;
	private final AccountType accountType;

	public enum AccountType {
		USER, COMPANY
	}

	public static CustomUserDetails from(User user) {
		return CustomUserDetails.builder()
			.id(user.getId())
			.username(user.getUsername())
			.password(user.getPassword() != null ? user.getPassword() : "")
			.role(user.getRole().name())
			.enabled(user.getStatus() == UserStatus.ACTIVE)
			.user(user)
			.company(null)
			.accountType(AccountType.USER)
			.build();
	}

	public static CustomUserDetails from(Company company) {
		return CustomUserDetails.builder()
			.id(company.getId())
			.username(company.getUsername())
			.password(company.getPassword())
			.role("COMPANY")
			.enabled(company.getStatus() == CompanyStatus.ACTIVE)
			.user(null)
			.company(company)
			.accountType(AccountType.COMPANY)
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

	public boolean isUser() {
		return accountType == AccountType.USER;
	}

	public boolean isCompany() {
		return accountType == AccountType.COMPANY;
	}
}
