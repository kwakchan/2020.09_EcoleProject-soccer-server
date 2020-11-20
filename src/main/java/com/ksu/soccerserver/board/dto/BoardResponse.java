package com.ksu.soccerserver.board.dto;

import com.ksu.soccerserver.account.Account;
import com.ksu.soccerserver.board.BoardType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class BoardResponse {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private BoardType boardType;
    private Account account;
}
