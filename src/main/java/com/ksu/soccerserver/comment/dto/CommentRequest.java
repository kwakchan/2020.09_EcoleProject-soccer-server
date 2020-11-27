package com.ksu.soccerserver.comment.dto;

import com.ksu.soccerserver.account.Account;
import com.ksu.soccerserver.board.Board;
import com.ksu.soccerserver.comment.Comment;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class CommentRequest {
    private Long boardId;
    private String content;

    public Comment toEntity(Board board, Account account) {
        return Comment.builder()
                .account(account)
                .board(board)
                .content(this.getContent())
                .createdAt(LocalDateTime.now())
                .build();
    }
}
