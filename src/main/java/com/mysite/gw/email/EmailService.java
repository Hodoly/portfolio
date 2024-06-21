package com.mysite.gw.email;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

	private final EmailRepository emailRepository;
	private final RedisTemplate redisTemplate;
	private final StringRedisTemplate stringRedisTemplate;

	@Transactional
	public EmailUser addUser(EmailUser user) {
		// save
		EmailUser save = emailRepository.save(user);

		// find
		Optional<EmailUser> result = emailRepository.findById(save.getId());

		// Handling
		// 해당 data 존재시 return.
		if (result.isPresent()) {
			return result.get();
		} else {
			throw new RuntimeException("Database has no Data");
		}
	}// save

	@Transactional(readOnly = true)
	public String getUserByEmail(String email) {
		String value = stringRedisTemplate.opsForValue().get(email);
//		Optional<RedisUser> result = repository.findByUsername(username);
		// Handling
		if (value != null || value != "") {
			return value;
		} else {
			return "Database has no Data";
		}

	}

	public String setRedis(String email, String serial) {
		stringRedisTemplate.opsForValue().set(email, serial, 5 * 60, TimeUnit.SECONDS);

		log.info("Temporay Password set : {}", redisTemplate.opsForValue().get(email));

		return (String) redisTemplate.opsForValue().get(email);
	}

	public String checkOtp(String id, String otp) {

		String target = id;

		if (stringRedisTemplate.hasKey(id)) {
			String value = stringRedisTemplate.opsForValue().get(target);

			if (value.equals(otp)) {
				log.info("OTP is Correct");
				return "SUCCESS";
			} else {
				return "FAIL";
			}
		} else {
			return "NO DATA";
		}
	}
}
