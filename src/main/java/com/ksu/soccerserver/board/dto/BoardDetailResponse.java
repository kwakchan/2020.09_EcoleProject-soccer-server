package com.ksu.soccerserver.board.dto;

import com.ksu.soccerserver.account.Account;
import com.ksu.soccerserver.board.BoardType;
import com.ksu.soccerserver.comment.Comment;
import com.ksu.soccerserver.comment.dto.CommentResponse;
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
    private Set<CommentResponse> comments = new HashSet<>();

    public void addComment(CommentResponse commentResponse) {
        this.comments.add(commentResponse);
    }
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
