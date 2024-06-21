package com.mysite.gw.email;

import org.springframework.data.repository.CrudRepository;

public interface EmailRepository extends CrudRepository<EmailUser, String> {
	
}
	