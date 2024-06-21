package com.mysite.gw;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

@Controller
public class MainController {
	@GetMapping("/")
	public String root() {
//		return "redirect:/question/list";
		// 현재 인증된 사용자 가져오기
//	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//	    boolean isAdmin = authentication.getAuthorities().stream()
//	                        .anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"));
//	    
//	    if (isAdmin) {
//	        return "admin_home";
//	    } else {
//	        return "home";
//	    }
	    return "home";
	}
	
	@GetMapping("/board")
	public String boardMain() {
		return "redirect:/board/list";
	}
}
