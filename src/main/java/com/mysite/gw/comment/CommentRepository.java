package com.mysite.gw.comment;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.mysite.gw.answer.Answer;
import com.mysite.gw.board.Board;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
	Page<Comment> findByBoard(Board board, Pageable pageable);

	ArrayList<Comment> findByBoard(Board board);
	ArrayList<Comment> findByAnswer(Answer answer);
}
