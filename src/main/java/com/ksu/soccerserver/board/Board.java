package com.ksu.soccerserver.board;

import com.ksu.soccerserver.account.Account;
import com.ksu.soccerserver.comment.Comment;
import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter @Builder @Setter
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
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime modifiedAt;

    @Enumerated(value =  EnumType.STRING)
    private BoardType boardType;

    @OneToMany(mappedBy = "board")
    private final Set<Comment> comment = new HashSet<>();


    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTime(LocalDateTime localDateTime) {
        this.modifiedAt = localDateTime;
    }

    public void setBoardtype(BoardType boardType) {
        this.boardType = boardType;
    }

    /*
    조회수
    게시자 팀이름
    게시날짜
    수정날짜
     */
}
