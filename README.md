# Spring Demo OAuth

2022년 6월 1일 부로 [Spring Security OAuth EOL](https://spring.io/blog/2022/06/01/spring-security-oauth-reaches-end-of-life) 되었으며 이제는 OAuth2 지원은 Spring Security 와 [Spring Authorization Server](https://spring.io/projects/spring-authorization-server)로 지원합니다.

## Dependencies
```groovy
implementation 'org.springframework.boot:spring-boot-starter-security'
implementation 'org.springframework.security:spring-security-oauth2-authorization-server:0.3.1'
implementation 'org.springframework.boot:spring-boot-starter-oauth2-resource-server'
implementation 'org.springframework.boot:spring-boot-starter-oauth2-client'
```

## Spring Authorization Server
[OAuth 2.1 Authorization Framework](https://datatracker.ietf.org/doc/html/draft-ietf-oauth-v2-1-05)와 [OpenID Connect Core 1.0](https://openid.net/specs/openid-connect-core-1_0.html)에 대한 기능을 지원하며 전체적인 지원 목록은 [Feature List](https://docs.spring.io/spring-authorization-server/docs/current/reference/html/overview.html#feature-list)에서 확인할 수 있습니다.

### SecurityFilterChain
애플리케이션 기본 인증 필터 체인보다 인증 서버의 프로토콜 엔드포인트에 대한 필터 체인을 우선하도록 명시해야합니다. 필터 체인 순서를 모른다면 Ordered.HIGHEST_PRECEDENCE를 고려하세요.

```java
@Slf4j
@Configuration(proxyBeanMethods = false)
public class AuthorizationServerConfig {
    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);

        http.exceptionHandling(exceptions ->
                exceptions.authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login")));
        return http.build();
    }
}

@EnableWebSecurity
public class DefaultSecurityConfig {
    @Bean
    @Order(2)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests(authorizeRequests ->
                        authorizeRequests.anyRequest().authenticated())
                .formLogin(withDefaults());
        return http.build();
    }
}
```

### ProviderSettings


### TokenSettings
OAuth 클라이언트에게 발급할 토큰에 대해 설정합니다. 만료 기간 또는 리프래시 토큰 유무등을 선택할 수 있습니다.

### RegisteredClientRepository
JdbcRegisteredClientRepository는 JdbcOperations와 RowMapper를 사용하여 JDBC 기반의 클라이언트 저장소를 지원합니다. 아쉽게도 Redis는 기본적으로 지원하지 않으니 직접 구현하셔야합니다.

## Links
- [Spring Authorization Server Docs](https://docs.spring.io/spring-authorization-server/docs/current/reference/html/getting-started.html)
- [Getting Started with Spring Authorization Server - SpringDeveloper](https://www.youtube.com/watch?v=ZIjqDIdFyBw)
- [Getting Started with Spring Authorization Server - sfjava](https://www.youtube.com/watch?v=3NliXoTcPSo&t=5879s)
- [Spring Security OAuth Authorization Server](https://www.baeldung.com/spring-security-oauth-auth-server)



