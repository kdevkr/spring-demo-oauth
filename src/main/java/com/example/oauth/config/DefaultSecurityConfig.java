package com.example.oauth.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

import static org.springframework.security.config.Customizer.withDefaults;

@Slf4j
@EnableWebSecurity
public class DefaultSecurityConfig {

    /**
     * Default SecurityFilterChain
     *
     * @param http {@link HttpSecurity}
     * @return {@link SecurityFilterChain}
     * @throws Exception
     */
    @Bean
    @Order(2)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.authorizeRequests(authorizeRequests ->
                        authorizeRequests
                                .anyRequest().authenticated()
                )
                .rememberMe().alwaysRemember(true)
                .and()
                .formLogin(withDefaults());
        return http.build();
    }

    /**
     * JdbcUserDetailsManager depends on {@link DataSourceInitializer} in {@link OAuthDatabasePopulator} for "users" tables.
     *
     * @param dataSource    {@link DataSource}
     * @param messageSource {@link MessageSource}
     * @return {@link UserDetailsService}
     */
    @DependsOn("dataSourceInitializer")
    @Bean
    public UserDetailsService userDetailsService(DataSource dataSource, MessageSource messageSource) {
        UserDetails user = User.builder()
                .username("user")
                .password("{bcrypt}$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW")
                .roles("USER")
                .build();
        UserDetails admin = User.builder()
                .username("admin")
                .password("{bcrypt}$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW")
                .roles("USER", "ADMIN")
                .build();

        JdbcUserDetailsManager users = new JdbcUserDetailsManager(dataSource);
        users.setMessageSource(messageSource);
        if (!users.userExists(user.getUsername())) users.createUser(user);
        if (!users.userExists(admin.getUsername())) users.createUser(admin);
        return users;
    }

}