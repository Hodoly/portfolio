package com.mysite.gw.oauth;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/login/oauth2", produces = "application/json")
public class GoogleController {

    GoogleService  googleService;

    public GoogleController(GoogleService googleService) {
        this.googleService = googleService;
    }

    @GetMapping("/code/{registrationId}")
    public void googleLogin(@RequestParam String code, @PathVariable String registrationId) {
    	googleService.socialLogin(code, registrationId);
    }
}