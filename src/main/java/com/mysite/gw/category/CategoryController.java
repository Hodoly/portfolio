package com.mysite.gw.category;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mysite.gw.DataNotFoundException;
import com.mysite.gw.answer.Answer;
import com.mysite.gw.answer.AnswerForm;
import com.mysite.gw.answer.AnswerService;
import com.mysite.gw.board.*;
import com.mysite.gw.user.SiteUser;
import com.mysite.gw.user.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequestMapping("/category")
@RequiredArgsConstructor
@Controller
public class CategoryController {
	private final CategoryService categoryService;

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/create")
	public String categoryCreate(CategoryForm categoryForm) {
		return "category_form";
	}
	
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/create")
	public String categoryCreate(@Valid CategoryForm categoryForm, BindingResult bindingResult, Principal principal) {
		if (bindingResult.hasErrors()) {
			return "category_form";
		}
		this.categoryService.create(categoryForm.getName());
		return "redirect:/question/list";
	}
}
