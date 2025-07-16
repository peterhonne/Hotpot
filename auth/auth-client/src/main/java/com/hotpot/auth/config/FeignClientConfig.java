package com.hotpot.auth.config;

import com.hotpot.auth.interceptor.FeignOauth2TokenInterceptor;
import feign.RequestInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Peter
 * @date 2023/3/9
 * @description
 */
@Configuration
@RequiredArgsConstructor
public class FeignClientConfig {

    @Bean
    public RequestInterceptor requestInterceptor(){
        return new FeignOauth2TokenInterceptor();
    }


}
