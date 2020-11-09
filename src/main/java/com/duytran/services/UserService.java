package com.duytran.services;

import com.duytran.constant.Message;
import com.duytran.entities.User;
import com.duytran.entities.UserInfo;
import com.duytran.models.ResponseModel;
import com.duytran.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.security.Principal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserService {

	private static final String USERNAME_REGEX = "^[a-zA-Z0-9]([._-](?![._-])|[a-zA-Z0-9]){3,18}[a-zA-Z0-9]$";

	@Autowired
	UserRepository userRepository;

	@Autowired
	MongoTemplate mongoTemplate;

	@Autowired
	UserInfoService userInfoService;

	@Autowired
	PasswordEncoder passwordEncoder;

	public User loadUserByUsername(String username) {
		return userRepository.findUserByUserName(username);
	}


	public ResponseModel registerUser(User user) {
		if (isValidateUsername(user.getUsername())) {
			return new ResponseModel(HttpStatus.BAD_REQUEST.value(), Message.USERNAME_INVALID, user);
		}
		if (user.getPassword() == null) {
			return new ResponseModel(HttpStatus.BAD_REQUEST.value(), Message.PASSWORD_NOT_ALLOW_NULL, user);
		}
		if (user.getRoles() == null) {
			return new ResponseModel(HttpStatus.BAD_REQUEST.value(), Message.ROLE_NOT_ALLOW_NULL, user);
		}
		if (userRepository.checkExistUserName(user.getUsername())) {
			return new ResponseModel(HttpStatus.BAD_REQUEST.value(), Message.USER_EXIST, user);
		}
		try {
			UserInfo userInfo = userInfoService.insertEmptyUserInformation();
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			user.setUserInfo(userInfo);
			mongoTemplate.insert(user);
			return new ResponseModel(HttpStatus.CREATED.value(), Message.SUCCESS , user);
		} catch (Exception e){
			return new ResponseModel(HttpStatus.BAD_REQUEST.value(), Message.FAIL, user);
		}
	}

	public boolean isValidateUsername(String info) {
		Pattern pattern = Pattern.compile(USERNAME_REGEX);
		if (info == null) {
			return true;
		}
		Matcher matcher = pattern.matcher(info);
		return !matcher.matches();
	}


	public String getUserNameByUserInfoId(String userInfoId) {
		User user = mongoTemplate.findOne(query(where("userInfo.id").is(userInfoId)), User.class);
		return user.getUsername();
	}

	public String getUserNameLogged() {
		Object principle = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (principle instanceof UserDetails) {
			return ((UserDetails) principle).getUsername();
		}
		if (principle instanceof Principal) {
			return ((Principal) principle).getName();
		}
		return String.valueOf(principle);
	}

}
