package com.mysite.gw.user;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mysite.gw.email.EmailUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {
	private final UserService userService;

	@Value("${spring.security.oauth2.client.registration.google.client-id}")
	private String clientId;

	@Value("${oauth2.google.redirect-uri}")
	private String redirectUri;

	@GetMapping("/signup")
	public String signup(UserCreateFormFst userCreateForm) {
		return "signup_fst";
	}

	@GetMapping("/pwfind")
	public String pwfind() {
		return "pwfind_form";
	}

	@GetMapping("/idfind")
	public String idfind() {
		return "idfind_form";
	}

	@GetMapping("/pwchange")
	public String pwchange(Model model, @RequestParam(value = "userid", defaultValue = "") String userid) {
		model.addAttribute("userid", userid);
		return "pw_change";
	}

	@PostMapping("/signup/fst")
	public String signupFst(@Valid UserCreateFormFst userCreateForm, BindingResult bindingResult, Model model) {
		if (bindingResult.hasErrors()) {
			return "signup_fst";
		}
		if (!userCreateForm.getPassword1().equals(userCreateForm.getPassword2())) {
			bindingResult.rejectValue("password2", "passwordInCorrect", "2개의 비밀번호가 일치하지 않습니다.");
			return "signup_fst";
		}
		model.addAttribute("username", userCreateForm.getUsername());
		model.addAttribute("nickname", userCreateForm.getNickname());
		model.addAttribute("password", userCreateForm.getPassword1());
		return "signup_sec";
	}

	@PostMapping("/signup/sec")
	public String signupSec(@Valid UserCreateFormSec userCreateForm, BindingResult bindingResult,
			HttpServletRequest req) {
		HttpSession session = req.getSession();
		if (session.getAttribute("authRecord") == null || session.getAttribute("authRecord") != "1") {
			bindingResult.rejectValue("auth", "noneAuth", "이메일 인증을 완료해주세요.");
			return "signup_sec";
		}
		try {
			userService.create(userCreateForm.getNickname(), userCreateForm.getUsername(), userCreateForm.getEmail(), userCreateForm.getPassword(),"0");
		} catch (DataIntegrityViolationException e) {
			e.printStackTrace();
			bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");
			return "signup_fst";
		} catch (Exception e) {
			e.printStackTrace();
			bindingResult.reject("signupFailed", e.getMessage());
			return "signup_fst";
		}
		return "redirect:/";
	}

	@PostMapping("/pwchange")
	public String pwchange(@Valid UserChangePw userChangePw, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return "pw_change";
		}
		if (!userChangePw.getPassword1().equals(userChangePw.getPassword2())) {
			bindingResult.rejectValue("password2", "passwordInCorrect", "2개의 비밀번호가 일치하지 않습니다.");
			return "pw_change";
		}
		try {
			userService.pwchange(userChangePw.getUsername(), userChangePw.getPassword1());
		} catch (Exception e) {
			e.printStackTrace();
			bindingResult.reject("signupFailed", e.getMessage());
			return "signup_fst";
		}
		return "redirect:/";
	}

	@GetMapping("/login")
	public String login() {
		return "login_form";
	}

	@GetMapping("/google/login")
	public String googleLogin() {
		String _uri = "https://accounts.google.com/o/oauth2/auth?client_id=" + clientId + "&redirect_uri=" + redirectUri
				+ "&response_type=code&scope=https://www.googleapis.com/auth/userinfo.email%20https://www.googleapis.com/auth/userinfo.profile";
		return "redirect:" + _uri;
	}
}
