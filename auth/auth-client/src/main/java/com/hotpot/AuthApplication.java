package com.hotpot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisIndexedHttpSession;


@EnableFeignClients
@SpringBootApplication
//@EnableRedisIndexedHttpSession(redisNamespace = "authClient:session")
@EnableRedisHttpSession(redisNamespace = "authClient:session")
public class AuthApplication {
    public static void main( String[] args )
    {
        SpringApplication.run(AuthApplication.class);
    }
}
