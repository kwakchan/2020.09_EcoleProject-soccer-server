package com.ksu.soccerserver.board.dto;

import com.ksu.soccerserver.board.BoardType;
import com.ksu.soccerserver.comment.dto.CommentResponse;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    private String image;
    private List<CommentResponse> comments = new ArrayList<>();

    public void addComment(CommentResponse commentResponse) {
        this.comments.add(commentResponse);
    }
}
