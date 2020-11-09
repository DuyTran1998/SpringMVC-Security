package com.duytran.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.internal.NotNull;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "users")
@Data
public class User {
	@Id
	private String id;

	private String username;

	private String password;

	@NotNull
	private Role[] roles;

	@DBRef(db = "userInfo", lazy = true)
	private UserInfo userInfo;

	public User() {
	}

	public User(String username, String password, Role[] roles) {
		this.username = username;
		this.password = password;
		this.roles = roles;
	}

	@JsonIgnore
	public List<GrantedAuthority> getAuthorities() {
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		for (Role role : roles) {
			authorities.add(new SimpleGrantedAuthority(role.toString()));
		}
		return authorities;
	}
}