package com.ksu.soccerserver.config;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/api/accounts/**").permitAll()
                .antMatchers("/api/teams/**").permitAll()
                .antMatchers("/api/apply/**").permitAll()
                .antMatchers("/api/invite/**").permitAll()
                .antMatchers("/h2-console/**").permitAll()
                .and().headers().frameOptions().disable()
                .and()
                .cors().and()
                .csrf().disable();
    }
}
