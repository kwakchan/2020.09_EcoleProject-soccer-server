package com.ksu.soccerserver.board.dto;

import com.ksu.soccerserver.account.Account;
import com.ksu.soccerserver.board.BoardType;
import com.ksu.soccerserver.comment.Comment;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class BoardDetailResponse {
    private Long id;
    private String title;
    private String name;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private BoardType boardType;
    private Set<Comment> comment = new HashSet<>();
}

/*
@Getter
@Setter
public class BoardListResponse {

    private Long id;
    private String title;
    private String Writer;
    private LocalDateTime createdAt;

}
 */
