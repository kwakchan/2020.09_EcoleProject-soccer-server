package com.ksu.soccerserver.comment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ksu.soccerserver.account.Account;
import com.ksu.soccerserver.board.Board;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Builder
@Entity
@Table
@NoArgsConstructor
@AllArgsConstructor
//@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")

public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Account account;

    @JsonIgnore
    @ManyToOne
    private Board board;

    @Column
    private String content;

    @Column
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime modifiedAt;

    public void setContent(String content) {
        this.content = content;
    }

    public void setTime(LocalDateTime localDateTime) {
        this.modifiedAt = localDateTime;
    }
}