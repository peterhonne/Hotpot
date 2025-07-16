package com.hotpot.auth.interceptor;

import com.hotpot.common.constant.SecurityConstant;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.token.DefaultOAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.JwtGenerator;

/**
 * @author Peter
 * @date 2023/3/9
 * @description
 */
public class FeignOauth2TokenInterceptor implements RequestInterceptor {

    private static final String BEARER = "Bearer";

    private static final String AUTHORIZATION = "Authorization";

    private static final Authentication ANONYMOUS_AUTHENTICATION = new AnonymousAuthenticationToken("anonymous",
            "anonymousUser", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));

    @Autowired
    private JWKSource<SecurityContext> jwkSource;
    @Autowired
    private RegisteredClientRepository registeredClientRepository;

    @Override
    public void apply(RequestTemplate template) {

        DefaultOAuth2TokenContext context = DefaultOAuth2TokenContext.builder().tokenType(OAuth2TokenType.ACCESS_TOKEN)
                .registeredClient(registeredClientRepository.findByClientId(SecurityConstant.AUTH_SERVER_CLIENT_REGISTRATION_ID))
                .principal(ANONYMOUS_AUTHENTICATION)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .authorizationGrant(ANONYMOUS_AUTHENTICATION).build();

        JwtGenerator jwtGenerator = new JwtGenerator(new NimbusJwtEncoder(jwkSource));
        Jwt token = jwtGenerator.generate(context);

        assert token != null;
        String extractedToken = String.format("%s %s", BEARER, token.getTokenValue());
        template.header(AUTHORIZATION);
        template.header(AUTHORIZATION, extractedToken);
    }
}
