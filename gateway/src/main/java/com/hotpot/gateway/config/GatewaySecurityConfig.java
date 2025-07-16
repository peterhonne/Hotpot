package com.hotpot.gateway.config;

import com.hotpot.gateway.filter.SingleSignOutWebFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.logout.DelegatingServerLogoutHandler;
import org.springframework.security.web.server.authentication.logout.RedirectServerLogoutSuccessHandler;
import org.springframework.security.web.server.authentication.logout.SecurityContextServerLogoutHandler;
import org.springframework.security.web.server.authentication.logout.WebSessionServerLogoutHandler;

import java.net.URI;

/**
 * @author Peter
 * @date 2023/2/24
 * @description
 */
@Configuration(enforceUniqueMethods = false)
@EnableWebFluxSecurity
public class GatewaySecurityConfig {


    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        DelegatingServerLogoutHandler logoutHandler = new DelegatingServerLogoutHandler(
                new WebSessionServerLogoutHandler(), new SecurityContextServerLogoutHandler()
        );
        RedirectServerLogoutSuccessHandler redirectServerLogoutSuccessHandler = new RedirectServerLogoutSuccessHandler();
        redirectServerLogoutSuccessHandler.setLogoutSuccessUrl(URI.create("/"));

        http
                .authorizeExchange(authorize -> authorize
                        .pathMatchers("/actuator/**", "/single-sign-out").permitAll()
                        .anyExchange().authenticated()
                )

                .logout(logoutConfig -> logoutConfig
                        .logoutHandler(logoutHandler)
                        .logoutSuccessHandler(redirectServerLogoutSuccessHandler)
                )

                // support single-sign-out
                .addFilterAfter(singleSignOutWebFilter(), SecurityWebFiltersOrder.LOGOUT)

                .oauth2Login(Customizer.withDefaults())
                .httpBasic().disable()
                .headers()
                .frameOptions().disable()
                .and()
                .csrf().disable();
        return http.build();
    }

    SingleSignOutWebFilter singleSignOutWebFilter() {
        return new SingleSignOutWebFilter();
    }




}
