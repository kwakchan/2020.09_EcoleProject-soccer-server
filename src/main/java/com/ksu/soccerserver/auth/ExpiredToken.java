package com.ksu.soccerserver.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder
@Entity
@Table
@Getter
@NoArgsConstructor  @AllArgsConstructor
public class ExpiredToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String token;

    @Column
    private LocalDateTime expiredTime;
}
