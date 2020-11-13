package com.ksu.soccerserver.board;

import com.ksu.soccerserver.account.Account;
import lombok.*;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

@Getter @Builder
@Entity
@Table
@NoArgsConstructor @AllArgsConstructor
public class Board {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Account account;

    @Column
    private String title;

    @Column
    private String content;

    @Column
    private LocalDateTime updatedatetime;

    @Column
    private String boardtype;


    public void boardaccount (Account account){
        this.account = account;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTime(LocalDateTime localDateTime) {
        this.updatedatetime = localDateTime;
    }

    public void setBoardtype(String boardtype) {
        this.boardtype = boardtype;
    }

    /*
    조회수
    게시자 팀이름
    게시날짜
    수정날짜
     */
}
