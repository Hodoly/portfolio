package com.mysite.gw.board;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import com.mysite.gw.answer.Answer;
import com.mysite.gw.answer.AnswerForm;
import com.mysite.gw.answer.AnswerService;
import com.mysite.gw.category.Category;
import com.mysite.gw.category.CategoryService;
import com.mysite.gw.comment.Comment;
import com.mysite.gw.user.SiteUser;
import com.mysite.gw.user.UserService;

import ch.qos.logback.core.recovery.ResilientSyslogOutputStream;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/question")
@RequiredArgsConstructor
@Controller
public class BoardController {
	private final BoardService boardService;
	private final AnswerService answerService;
	private final UserService userService;
	private final CategoryService categoryService;

	@GetMapping("/list")
	public String list(Model model, @RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "kw", defaultValue = "") String kw,
			@RequestParam(value = "ct", defaultValue = "0") int ct) {
		Page<Board> paging = this.boardService.getList(page, kw, ct);
		List<Category> category = this.categoryService.getCategory();
		model.addAttribute("category", category);
		model.addAttribute("paging", paging);
		model.addAttribute("kw", kw);
		model.addAttribute("ct", ct);

		// 현재 인증된 사용자 가져오기
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		boolean isAdmin = authentication.getAuthorities().stream()
				.anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"));

		if (isAdmin) {
			return "admin_question_list";
		} else {
			return "question_list";
		}
	}

	@GetMapping(value = "/detail/{id}")
	public String detail(Model model, @PathVariable("id") Integer id, AnswerForm answerForm,
			@RequestParam(value = "page", defaultValue = "0") int page) {
		Board question = this.boardService.getBoard(id);
		Page<Answer> paging = this.answerService.getAnswer(question, page);
		Category category = question.getCategory();
		model.addAttribute("category", category.getName());
		model.addAttribute("paging", paging);
		model.addAttribute("question", question);
		model.addAttribute("commentForm", new Comment());
		return "question_detail";
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/create")
	public String questionCreate(Model model, BoardForm boardForm) {
		List<Category> category = this.categoryService.getCategory();
		model.addAttribute("category", category);
		return "question_form";
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/create")
	public String questionCreate(@Valid BoardForm questionForm, BindingResult bindingResult, Principal principal) {
		if (bindingResult.hasErrors()) {
			return "question_form";
		}
		SiteUser siteUser = this.userService.getUser(principal.getName());
		this.boardService.create(questionForm.getSubject(), questionForm.getContent(), siteUser,
				questionForm.getCategory());
		return "redirect:/question/list";
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/modify/{id}")
	public String questionModify(BoardForm questionForm, @PathVariable("id") Integer id, Principal principal) {
		Board question = this.boardService.getBoard(id);
		if (!question.getAuthor().getUsername().equals(principal.getName())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다.");
		}
		questionForm.setSubject(question.getSubject());
		questionForm.setContent(question.getContent());
		return "question_form";
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/modify/{id}")
	public String questionModify(@Valid Board questionForm, BindingResult bindingResult, Principal principal,
			@PathVariable("id") Integer id) {
		if (bindingResult.hasErrors()) {
			return "question_form";
		}
		Board question = this.boardService.getBoard(id);
		if (!question.getAuthor().getUsername().equals(principal.getName())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다.");
		}
		this.boardService.modify(question, questionForm.getSubject(), questionForm.getContent());
		return String.format("redirect:/question/detail/%s", id);
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/delete/{id}")
	public String questionDelete(Principal principal, @PathVariable("id") Integer id) {
		Board question = this.boardService.getBoard(id);
		if (!question.getAuthor().getUsername().equals(principal.getName())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제 권한이 없습니다.");
		}
		this.boardService.delete(question);
		return "redirect:/";
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/vote/{id}")
	public String questionVote(Principal principal, @PathVariable("id") Integer id) {
		Board question = this.boardService.getBoard(id);
		SiteUser siteUser = this.userService.getUser(principal.getName());
		this.boardService.vote(question, siteUser);
		return String.format("redirect:/question/detail/%s", id);
	}
}
