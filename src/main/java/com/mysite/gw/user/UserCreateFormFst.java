package com.mysite.gw.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreateFormFst {
	@Size(min=3, max=25)
	@NotEmpty(message = "사용자는 ID는 필수 항목입니다.")
	private String username;
	
	@NotEmpty(message = "이름은 필수 항목입니다.")
	private String nickname;
	
	@NotEmpty(message="비밀번호는 필수 항목입니다.")
	private String password1;
	
	@Size(min=4, max=20)
	@NotEmpty(message="비밀번호는 필수 항목입니다.")
	private String password2;
	
}
