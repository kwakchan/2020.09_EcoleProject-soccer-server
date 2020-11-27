package com.ksu.soccerserver.config;

import com.ksu.soccerserver.auth.ExpiredTokenRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
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
    private final ExpiredTokenRepository expiredTokenRepository;

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

    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
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
                /*
                //TODO - api/accounts(POST)는 permitAll, api/accounts?name={}&{}&.. 는 hasRole() 가능여부
                .antMatchers("api/accounts/join").permitAll() //회원가입 허가
                .antMatchers("/api/accounts/**").hasRole("USER") //인증요구
                 */
                .antMatchers("/api/accounts").permitAll() //회원가입 허가
                .antMatchers(HttpMethod.GET,"/api/accounts/images/**").permitAll() //유저 이미지 허가
                .antMatchers(HttpMethod.GET,"/api/teams/images/**").permitAll() //팀 이미지 허가
                .antMatchers("/api/accounts/**").hasRole("USER") //Account(Modify, Remove, Load) 인증요구
                .antMatchers("/api/login", "/api/logout").permitAll() //Login/out 허가
                .antMatchers("/api/find/**").permitAll() //회원정보 찾기 허가
                .antMatchers("/api/teams/**").hasRole("USER") //인증요구
                //아래는 위 코드 pull받은 후 추가 및 변경사항에 대해 hasRole해줄 것.
                .antMatchers("/api/apply/**").hasRole("USER") //인증요구
                .antMatchers("/api/invite/**").hasRole("USER") //인증요구
                .antMatchers("/api/boards/**").hasRole("USER") //인증요구
                .antMatchers("/h2-console/**").permitAll() //개발 편의상 permitAll
                .and().headers().frameOptions().disable()
                .and()
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, expiredTokenRepository), UsernamePasswordAuthenticationFilter.class)
                //JwtAuthenticationFilter를 UserPasswordAuthenticationFilter전에 넣는다.
                .exceptionHandling().authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
    }

}
