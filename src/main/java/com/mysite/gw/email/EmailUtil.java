package com.mysite.gw.email;

import java.util.Map;

public interface EmailUtil {
	 Map<String, Object> sendEmailPw(String username, String subject, String body, String serial);
	 Map<String, Object> sendEmailSign(String email, String subject, String body, String serial);
}
