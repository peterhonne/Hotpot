package com.hotpot.gateway.filter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.log.LogMessage;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.web.server.DefaultServerRedirectStrategy;
import org.springframework.security.web.server.ServerRedirectStrategy;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.logout.SecurityContextServerLogoutHandler;
import org.springframework.security.web.server.authentication.logout.ServerLogoutHandler;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.net.URI;

/**
 * @author Peter
 * @date 2023/3/13
 * @description
 */
public class SingleSignOutWebFilter implements WebFilter {

    private static final Log logger = LogFactory.getLog(SingleSignOutWebFilter.class);

    private ServerRedirectStrategy authorizationRedirectStrategy = new DefaultServerRedirectStrategy();

    private final ServerWebExchangeMatcher matcher = ServerWebExchangeMatchers.pathMatchers(HttpMethod.GET, "/single-sign-out");

    private final AnonymousAuthenticationToken anonymousAuthenticationToken = new AnonymousAuthenticationToken("key",
            "anonymous", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));

    private final ServerLogoutHandler logoutHandler = new SecurityContextServerLogoutHandler();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        return this.matcher.matches(exchange).filter(ServerWebExchangeMatcher.MatchResult::isMatch)
                .switchIfEmpty(chain.filter(exchange).then(Mono.empty())).map(result -> exchange)
                .flatMap(this::flatMapAuthentication).flatMap(authentication -> {
                    WebFilterExchange webFilterExchange = new WebFilterExchange(exchange, chain);
                    // logout current system first
                    return logout(webFilterExchange, authentication)
                            // then redirect to auth-server/logout
                            .then(authorizationRedirectStrategy.sendRedirect(exchange, URI.create("http://127.0.0.1:9081/logout")));
                });
    }

    private Mono<Authentication> flatMapAuthentication(ServerWebExchange exchange) {
        return exchange.getPrincipal().cast(Authentication.class).defaultIfEmpty(this.anonymousAuthenticationToken);
    }

    private Mono<Void> logout(WebFilterExchange webFilterExchange, Authentication authentication) {
        logger.debug(LogMessage.format("Logging out user '%s' and transferring to logout destination", authentication));
        return this.logoutHandler.logout(webFilterExchange, authentication)
                .contextWrite(ReactiveSecurityContextHolder.clearContext());
    }
}
