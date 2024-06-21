package com.mysite.gw.email;

import org.springframework.data.redis.core.RedisHash;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import jakarta.persistence.Id;

@RedisHash("redisUser") // options: timeToLive = 10
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Email {
    @Id
    private String id; // userId: 입력안하면 임의의 값 생성됨.

    private String username;
    
    private String serial; 

}