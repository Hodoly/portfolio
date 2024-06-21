package com.mysite.gw.user;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mysite.gw.DataNotFoundException;
import com.mysite.gw.email.Email;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	
	public SiteUser create(String username, String email, String password) {
		SiteUser user = new SiteUser();
		user.setUsername(username);
		user.setEmail(email);
		user.setPassword(passwordEncoder.encode(password));
		this.userRepository.save(user);
		return user;
	}
	
	public SiteUser pwchange(String username, String password) {
		Optional<SiteUser> result = userRepository.findByUsername(username);
		SiteUser user = result.get();
		user.setPassword(passwordEncoder.encode(password));
		this.userRepository.save(user);
		return user;
	}
	
	public SiteUser getUser(String username) {
		Optional<SiteUser> siteUser = this.userRepository.findByUsername(username);
		if(siteUser.isPresent()) {
			return siteUser.get();
		}else {
			throw new DataNotFoundException("siteuser not found");
		}
	}
	public SiteUser getUserByEmail(String email) {
		Optional<SiteUser> siteUser = this.userRepository.findByEmail(email);
		if(siteUser.isPresent()) {
			return siteUser.get();
		}else {
			throw new DataNotFoundException("siteuser not found");
		}
	}
	public Integer getUserEmail(String email) {
		Optional<SiteUser> siteUser = this.userRepository.findByEmail(email);
		if(siteUser.isPresent()) {
			return 1;
		}else {
			return 0;
		}
	}
}
