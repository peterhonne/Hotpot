package com.hotpot.auth.config;

import com.hotpot.common.constant.SecurityConstant;
import com.hotpot.common.dto.SysUserDTO;
import com.hotpot.auth.feign.UserServiceApi;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import java.io.IOException;

/**
 * @author Peter
 * @date 2023/3/8
 * @description
 */
@RequiredArgsConstructor
public class UserAuthenticationSuccessHandler implements AuthenticationSuccessHandler {


    private final UserServiceApi userServiceApi;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final AuthenticationSuccessHandler delegate = new SavedRequestAwareAuthenticationSuccessHandler();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if (authentication instanceof OAuth2AuthenticationToken && authentication.getPrincipal() instanceof OAuth2User) {
            DefaultOAuth2User defaultOAuth2User = (DefaultOAuth2User) authentication.getPrincipal();
            // save new user to db
            if (defaultOAuth2User.getAuthorities().contains(new SimpleGrantedAuthority(SecurityConstant.ROLE_NEW_USER))) {
                SysUserDTO user = new SysUserDTO(defaultOAuth2User.getName(), passwordEncoder.encode("rawpassword"), defaultOAuth2User.getName());
                userServiceApi.save(user);
                // TODO save default role here or in UserService
            }
        }

        this.delegate.onAuthenticationSuccess(request, response, authentication);
    }


}
