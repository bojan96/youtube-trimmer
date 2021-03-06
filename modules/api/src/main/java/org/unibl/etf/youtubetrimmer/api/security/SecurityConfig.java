package org.unibl.etf.youtubetrimmer.api.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtAuthenticationFilter authFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.httpBasic()
                .disable()
                .csrf()
                .disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.cors();
        http.addFilterAt(authFilter, BasicAuthenticationFilter.class);
        http.authorizeRequests().mvcMatchers("/api/auth/login").permitAll();
        http.authorizeRequests().mvcMatchers("/api/auth/token/refresh").authenticated();
        http.authorizeRequests().mvcMatchers("/api/job", "/api/job/*/cancel").authenticated();
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().mvcMatchers("/stomp");
    }
}
