package com.ksu.soccerserver.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

//WebSecurityConfigureAdapter, @EnableWebSecurity
//=>Spring Security Filter Chain을 사용함을 명시
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtTokenProvider jwtTokenProvider;

    //Bean - Spring FW를 통해 생성되고 관리되는 객체
    //암호화에 필요한 PasswordEncoder를 Bean등록한다.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    //authenticationManager를 Bean등록한다.
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable() //rest API만을 고려. 기본설정x.
                //.cors().and() //?
                .csrf().disable() // csrf보안 토큰 disable처리
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                //세션 사용 X
                .and()
                .authorizeRequests() //요청에 대한 사용권한 체크
                .antMatchers("/api/admin/**").hasRole("ADMIN") //인증요구
                .antMatchers("/api/accounts/join").permitAll() //인증요구
                .antMatchers("/api/accounts/login").permitAll() //인증요구
                .antMatchers("/api/accounts/**").hasRole("USER") //인증요구
                .antMatchers("/api/teams/**").hasRole("USER") //인증요구
                .antMatchers("/api/apply/**").hasRole("USER") //인증요구
                .antMatchers("/api/invite/**").hasRole("USER") //인증요구
                .antMatchers("/h2-console/**").permitAll() //개발 편의상 permitAll
                .and().headers().frameOptions().disable()
                .and()
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling().authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
                //JwtAuthenticationFilter를 UserPasswordAuthenticationFilter전에 넣는다.
    }
}
