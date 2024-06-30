package com.mysite.gw.oauth;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.mysite.gw.DataNotFoundException;
import com.mysite.gw.user.SiteUser;
import com.mysite.gw.user.UserRepository;
import com.mysite.gw.user.UserService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
	private final UserRepository userRepository;
	private final UserService userService;
	
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2User oAuth2User = super.loadUser(userRequest);
		String nickname = oAuth2User.getAttribute("name");
		String id = oAuth2User.getAttribute("id");
		String email = oAuth2User.getAttribute("email");
		String password = "";

		SiteUser user;
		if (getUserCheckOauth(email)) {
			user = userService.getUserByEmail(email, "1");
		} else {
//			user = oauthCreate(nickname, id, email, password);
			user = userService.create(nickname, id, email, password, "1");
		}
		
		List<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

		// 폼 로그인 처리
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
				user.getUsername(), user.getPassword(), authorities);
		SecurityContextHolder.getContext().setAuthentication(authenticationToken);

		return oAuth2User;
	}

	public boolean getUserCheckOauth(String email) {
		Optional<SiteUser> SiteUser = this.userRepository.findByUsernameAndType(email, "1");
		if (SiteUser.isPresent()) {
			return true;
		} else {
			return false;
		}
	}

}
