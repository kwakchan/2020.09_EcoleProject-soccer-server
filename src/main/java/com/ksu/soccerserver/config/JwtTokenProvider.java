package com.ksu.soccerserver.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
@Component
//JWT 생성 & 검증
public class JwtTokenProvider {

    private String secretKey = "ecole-KS";

    //토큰 유효시간 30분
    private long tokenValidTime = 30 * 60 * 1000L;

    //DB에서 사용자 인증정보를 가져오는 객체
    //private final UserDetailsService userDetailsService = null; //?
    private final UserDetailsService userDetailsService;

    //객체 초기화, secretKey를 Base64로 Encoding
    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    //JWT Token 생성
    public String createToken(String userPk, List<String> roles) {
        //Claims = JWT의 Payload에 담을 정보의 한 '조각' 단위
        Claims claims = Jwts.claims().setSubject(userPk); //등록된 클레임(subject)
        claims.put("roles", roles); //비공개 클레임 'key : value'
        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims) //정보 저장
                .setIssuedAt(now) //토큰 발행 시간 정보(iat)
                .setExpiration(new Date(now.getTime() + tokenValidTime)) //토큰의 만료시간(exp)
                .signWith(SignatureAlgorithm.HS256, secretKey) //HS256으로 암호화
                .compact();

    }

    //JWT토큰으로부터 인증 정보 조회
    public Authentication getAuthentication(String token) {
        //사용자 정보 싣고 다니는 객체
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUserPk(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());

    }

    //토큰에서 회원 정보 추출
    public String getUserPk(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }

    //Request의 Header에서 token값을 가져온다.
    //"X-AUTH-TOKEN" : "TOKEN..."
    public String resolveToken(HttpServletRequest request) {
        return request.getHeader("Authorization");
    }

    // 토큰 유효성 + 만료일자 확인
    public boolean validateToken(String jwtToken) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }
}
