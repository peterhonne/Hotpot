package com.hotpot.auth.resource;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.*;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.security.oauth2.server.resource.introspection.SpringOpaqueTokenIntrospector;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.RequestMatcherDelegatingAuthenticationManagerResolver;
import org.springframework.security.web.authentication.logout.CookieClearingLogoutHandler;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcherEntry;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Supplier;

/**
 * @author Peter
 * @date 2023/2/24
 * @description
 */
@Configuration(enforceUniqueMethods = false)
@EnableWebSecurity
public class ResourceServerConfig {


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorize ->
                        authorize.requestMatchers("/actuator/**").permitAll()
                                .anyRequest().authenticated());

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .and()
                .httpBasic().disable()
                .headers()
                .frameOptions().disable()
                .and()
                .csrf().disable();
        http.logout().deleteCookies("JSESSIONID", "SESSION").invalidateHttpSession(true).clearAuthentication(true);
        http.oauth2ResourceServer()
                .jwt().jwtAuthenticationConverter(jwtAuthenticationConverter());
        return http.build();
    }

    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");
        jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName("authorities");
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }


    /**
     * code bellow supports multi-tenancy with dynamic tenants
     *
     * https://docs.gitcode.net/spring/guide/en/spring-security/servlet-oauth2-resource-server-multitenancy.html#multi-tenancy
     */
//    @Bean
//    AuthenticationManagerResolver<HttpServletRequest> multiTenantAuthenticationManager() {
//        Map<String, AuthenticationManager> authenticationManagers = loadAuthenticationManagers();
//        return (request) -> {
//            String tenantId = request.getHeader("tenant_id");
//            return Optional.ofNullable(tenantId)
//                    .map(authenticationManagers::get)
//                    .orElseThrow(() -> new IllegalArgumentException("unknown tenant"));
//        };
//    }

    /**
     * Supporting both JWT and Opaque Token
     * @return
     */
    AuthenticationManagerResolver<HttpServletRequest> hybridAuthenticationManager() {
        List<String> readMethod = List.of("GET");
        RegisteredClientConfig jwt = new RegisteredClientConfig("auth-server-jwt", "{noop}secret", "tenant one", "jwt", "http://localhost:9081/oauth2/jwks");
        RegisteredClientConfig opaque = new RegisteredClientConfig("auth-server-opaque", "{noop}secret", "tenant two", "opaque", "http://127.0.0.1:9081/oauth2/introspect");

        // USE JWT tokens (locally validated) to validate HEAD, GET, and OPTIONS requests
        // all other requests will use opaque tokens (remotely validated)
        RequestMatcher readMethodRequestMatcher = request -> readMethod.contains(request.getMethod());
        RequestMatcherDelegatingAuthenticationManagerResolver authenticationManagerResolver
                = RequestMatcherDelegatingAuthenticationManagerResolver.builder().add(readMethodRequestMatcher, jwt(jwt.verifyUri)).build();

        // Use opaque tokens (remotely validated) for other requests
        authenticationManagerResolver.setDefaultAuthenticationManager(opaque(new SpringOpaqueTokenIntrospector(opaque.verifyUri, opaque.clientId,
                opaque.clientSecret)));
        return authenticationManagerResolver;
    }

    /**
     * TODO load RegisteredClientConfig from redis
     * @return
     */
    Map<String, AuthenticationManager> loadAuthenticationManagers() {
        Map<String, AuthenticationManager> authenticationManagers = new HashMap<>();
        // testing data
        RegisteredClientConfig jwt = new RegisteredClientConfig("auth-server-jwt", "{noop}secret", "tenant one", "jwt", "http://localhost:9081/oauth2/jwks");
        RegisteredClientConfig opaque = new RegisteredClientConfig("auth-server-opaque", "{noop}secret", "tenant two", "opaque", "http://127.0.0.1:9081/oauth2/introspect");
        RegisteredClientConfig keycloakJwt = new RegisteredClientConfig("messaging-client", "{noop}secret", "tenant three", "jwt", "http://127.0.0.1:8080/realms/oauth2-sample/protocol/openid-connect/certs");
        List<RegisteredClientConfig> list = new ArrayList<>();
        list.add(jwt);
        list.add(opaque);
        list.add(keycloakJwt);
        // testing data
        for(RegisteredClientConfig config : list) {
            String tokenType = config.tokenType;
            AuthenticationManager provider = null;
            if ("jwt".equals(tokenType)) {
                provider = jwt(config.verifyUri);
            } else if ("opaque".equals(tokenType)) {
                provider = opaque(new SpringOpaqueTokenIntrospector(config.verifyUri, config.clientId,
                        config.clientSecret));
            }
            authenticationManagers.put(config.clientId, provider);
        }

        return authenticationManagers;
    }
    record RegisteredClientConfig(String clientId, String clientSecret, String tenantId, String tokenType, String verifyUri) {}


    AuthenticationManager jwt(String jwkSetUri) {
        NimbusJwtDecoder nimbusJwtDecoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
        Supplier<OAuth2TokenValidator<Jwt>> defaultValidator = JwtValidators::createDefault;
        nimbusJwtDecoder.setJwtValidator(getValidators(defaultValidator));

        JwtAuthenticationProvider authenticationProvider = new JwtAuthenticationProvider(nimbusJwtDecoder);
        authenticationProvider.setJwtAuthenticationConverter(jwtAuthenticationConverter());
        return new ProviderManager(authenticationProvider);
    }

    private OAuth2TokenValidator<Jwt> getValidators(Supplier<OAuth2TokenValidator<Jwt>> defaultValidator) {
        OAuth2TokenValidator<Jwt> defaultValidators = defaultValidator.get();
        List<String> audiences = new ArrayList<>(); // TODO config the spring.security.oauth2.resourceserver.jwt.audiences property
        if (CollectionUtils.isEmpty(audiences)) {
            return defaultValidators;
        }
        List<OAuth2TokenValidator<Jwt>> validators = new ArrayList<>();
        validators.add(defaultValidators);
//        validators.add(new JwtIssuerValidator(issuer)); // if with issuer configured
        validators.add(new JwtClaimValidator<List<String>>(JwtClaimNames.AUD,
                (aud) -> aud != null && !Collections.disjoint(aud, audiences)));
        return new DelegatingOAuth2TokenValidator<>(validators);
    }

    AuthenticationManager opaque(OpaqueTokenIntrospector introspectionClient) {

        return new ProviderManager(new OpaqueTokenAuthenticationProvider(introspectionClient));
    }

}
