package com.mysite.gw.oauth;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mysite.gw.user.SiteUser;

public interface OauthUserRepository extends JpaRepository<OauthUser, Long> {
	Optional<OauthUser> findByUsername(String username);
	Optional<OauthUser> findByEmail(String email);
}
