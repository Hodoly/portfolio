package com.mysite.gw.comment;

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
import com.mysite.gw.board.Board;
import com.mysite.gw.board.BoardService;
import com.mysite.gw.user.SiteUser;
import com.mysite.gw.user.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequestMapping("/comment")
@RequiredArgsConstructor
@Controller
public class CommentController {
	private final BoardService boardService;
	private final AnswerService answerService;
	private final CommentService commentService;
	private final UserService userService;

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/create/q/{id}")
	public String commentCreateQuestion(Model model, @PathVariable("id") Integer id, @Valid CommentForm commentForm,
			BindingResult bindingResult, Principal principal) {
		Board board = this.boardService.getBoard(id);
		SiteUser siteUser = this.userService.getUser(principal.getName());
		if (bindingResult.hasErrors()) {
			model.addAttribute("commentForm", new Comment());
			model.addAttribute("question", board);
			return "question_detail";
		}
		this.commentService.create(board, null, commentForm.getContent(), siteUser, "1");
		return String.format("redirect:/question/detail/%s", board.getId());
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/create/a/{id}")
	public String commentCreateAnswer(Model model, @PathVariable("id") Integer id, @Valid CommentForm commentForm,
			BindingResult bindingResult, Principal principal) {
		Answer answer = this.answerService.getAnswer(id);
		Board board = answerService.getAnswer(id).getBoard();
		SiteUser siteUser = this.userService.getUser(principal.getName());
		if (bindingResult.hasErrors()) {
			model.addAttribute("commentForm", new Comment());
			model.addAttribute("answer", answer);
			return "question_detail";
		}
		this.commentService.create(null, answer, commentForm.getContent(), siteUser, "2");
		return String.format("redirect:/question/detail/%s", board.getId());
	}

	@GetMapping("/list/{kind}/{id}/{page}")
	@ResponseBody
	public String getQuestionComment(@PathVariable("kind") String kind,@PathVariable("id") Integer id, @PathVariable("page") Integer page) {
		ArrayList<Comment> comment = null;
		if(kind.equals("q")) {
			Board board = this.boardService.getBoard(id);
			comment = this.commentService.getQuestionComment(board);
		}else {
			Answer answer = this.answerService.getAnswer(id);
			comment = this.commentService.getAnswerComment(answer);
		}
		
		ArrayList<CommentDTO> dto = new ArrayList<CommentDTO>();
		int start = page * 3;
		int end;
		if(comment.size()<start+3) {
			end = comment.size()-1;
		}else {
			end = page+2;
		}
		
		for(int i=start; i<=end; i++) {
			var j=0;
			try {
				dto.add(j,CommentDTO.toDTO(comment.get(i)));
				j = j + 1;
			}catch(DataNotFoundException e){
				e.printStackTrace();
			}
			
		}
		Gson gson = new Gson();
		return gson.toJson(dto);
	}
	
}
