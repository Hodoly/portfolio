package com.mysite.gw.comment;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mysite.gw.answer.Answer;
import com.mysite.gw.board.Board;
import com.mysite.gw.user.SiteUser;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.ManyToOne;

@Getter
@Setter
@Entity
public class Comment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(columnDefinition = "TEXT")
	private String content;

	@CreatedDate
	private LocalDateTime createDate;

	@ManyToOne
	private Board board;
	
	@ManyToOne
	private Answer answer;

	@ManyToOne
	private SiteUser author;
	
	@Column(columnDefinition = "TEXT")
	private String kind;
}