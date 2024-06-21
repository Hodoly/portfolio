package com.mysite.gw.comment;
import groovy.transform.ToString;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {
	private String id;
	private String content;
	private String createDate;
	private String userName;
	private Long userId; 
	private String kind;
	
    public static CommentDTO toDTO(Comment entity) {
        return CommentDTO.builder()
        		.id(entity.getId().toString())
                .content(entity.getContent())
                .createDate(entity.getCreateDate().toString())
                .userName(entity.getAuthor().getUsername())
                .userId(entity.getAuthor().getId())
                .kind(entity.getKind())
                .build();
    }
}
