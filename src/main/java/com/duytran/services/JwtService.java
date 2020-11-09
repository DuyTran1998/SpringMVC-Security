package com.duytran.services;

import com.duytran.entities.User;
import com.duytran.security.CipherUtility;
import com.duytran.security.KeyPair;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.PublicKey;
import java.util.Base64;
import java.util.Date;

@Service
public class JwtService {

	@Autowired
	UserService userService;

	@Autowired
	CipherUtility cipherUtility;

	public static final String USERNAME = "username";
	public static final String AUTHOR = "author";
	public static final int EXPIRE_TIME = 86_400_000;


	public String generateTokenLogin(String username) {
		User user = userService.loadUserByUsername(username);
		Date now = new Date();
		Date expTime = new Date(now.getTime() + EXPIRE_TIME);
//		PublicKey publicKey = cipherUtility.decodePublicKey(KeyPair.PUBLIC_KEY);
		return Jwts.builder()
				.claim(USERNAME, username)
				.claim(AUTHOR, user.getAuthorities())
				.setExpiration(expTime)
				.signWith(SignatureAlgorithm.RS256, cipherUtility.decodePrivateKey(KeyPair.PRIVATE_KEY))
				.compact();
	}


	public String getUsernameFromToken(String token) {
		Claims claims = Jwts.parser()
				.setSigningKey(cipherUtility.decodePublicKey(KeyPair.PUBLIC_KEY))
				.parseClaimsJws(token)
				.getBody();
		return (String) claims.get("username");
	}

	public boolean validateToken(String authToken) {
		try {
			Jwts.parser().setSigningKey(cipherUtility.decodePublicKey(KeyPair.PUBLIC_KEY)).parseClaimsJws(authToken);
			return true;
		} catch (MalformedJwtException | ExpiredJwtException | UnsupportedJwtException | IllegalArgumentException ex) {
			return false;
		}
	}
}
