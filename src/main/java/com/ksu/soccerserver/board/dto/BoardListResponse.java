package com.ksu.soccerserver.board.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ksu.soccerserver.account.Account;
import com.ksu.soccerserver.account.AccountRepository;
import com.ksu.soccerserver.board.BoardRepository;
import com.ksu.soccerserver.board.BoardType;
import com.ksu.soccerserver.comment.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class BoardListResponse {
    private Long id;
    private String title;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private BoardType boardType;
    //private Set<Comment> comment = new HashSet<>();

}
