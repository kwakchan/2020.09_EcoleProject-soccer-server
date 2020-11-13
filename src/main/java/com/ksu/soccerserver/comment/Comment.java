package com.ksu.soccerserver.comment;

import com.ksu.soccerserver.account.Account;
import com.ksu.soccerserver.board.Board;
import lombok.*;

import javax.persistence.*;

@Getter
@Builder
@Entity
@Table
@NoArgsConstructor
@AllArgsConstructor

public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Account account;

    @ManyToOne
    private Board board;

    @Column
    private String content;

    @Column
    private String time;

    public void commentAccount (Account account) { this.account = account;}
    public void commentBoard (Board board) { this.board = board;}

    public void setContent(String content) {
        this.content = content;
    }

    public void setTime(String format) {
        this.time = time;
    }
}