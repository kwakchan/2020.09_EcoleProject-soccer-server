package com.ksu.soccerserver.board.dto;

import com.ksu.soccerserver.board.BoardType;
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
    private BoardType boardType;
}
