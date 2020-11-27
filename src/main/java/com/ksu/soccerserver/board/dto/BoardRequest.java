package com.ksu.soccerserver.board.dto;

import com.ksu.soccerserver.account.Account;
import com.ksu.soccerserver.board.Board;
import com.ksu.soccerserver.board.BoardType;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class BoardRequest {
    private String title;
    private String content;
    private BoardType boardType;

    public Board toEntity(Account account) {
        return Board.builder()
                .title(this.getTitle())
                .content(this.getContent())
                .createdAt(LocalDateTime.now())
                .boardType(this.getBoardType())
                .account(account)
                .build();
    }
}
