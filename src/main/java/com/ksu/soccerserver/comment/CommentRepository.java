package com.ksu.soccerserver.comment;

import com.ksu.soccerserver.board.Board;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByBoard(Board board);
}
