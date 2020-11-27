package com.ksu.soccerserver.board.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

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
    private String boardType;
}
