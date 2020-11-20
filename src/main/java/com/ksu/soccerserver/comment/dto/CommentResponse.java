package com.ksu.soccerserver.comment.dto;

import com.ksu.soccerserver.account.Account;
import com.ksu.soccerserver.board.Board;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CommentResponse {
    private Long id;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private Board board;
    private Account account;
}
