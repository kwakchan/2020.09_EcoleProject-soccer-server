package com.ksu.soccerserver.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;

import javax.persistence.*;

@Builder
@Entity
@Table
@Getter
@NoArgsConstructor  @AllArgsConstructor
public class LogoutAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String token;

}
