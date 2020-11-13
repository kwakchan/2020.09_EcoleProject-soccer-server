package com.ksu.soccerserver.comment;

import com.ksu.soccerserver.account.Account;
import com.ksu.soccerserver.account.AccountRepository;
import com.ksu.soccerserver.board.Board;
import com.ksu.soccerserver.board.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RequestMapping("/api/boards")
@RequiredArgsConstructor
@RestController
public class CommentController {
    private final CommentRepository commentRepository;
    private final AccountRepository accountRepository;
    private final BoardRepository boardRepository;
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @PostMapping("/{boardId}/comment/{accountId}")
    ResponseEntity<?> postComment(@RequestBody Comment comment, @PathVariable Long boardId, @PathVariable Long accountId) {
        Date date = new Date();

        Account account = accountRepository.findById(accountId).get();
        Board board = boardRepository.findById(boardId).get();
        Comment savecomment = commentRepository.save(Comment.builder()
                .content(comment.getContent())
                .time(format.format(date))
                .build());

        savecomment.commentAccount(account);
        savecomment.commentBoard(board);

        commentRepository.save(savecomment);
        return new ResponseEntity<>("Create new Comments" + board.getId() + "번째 게시판", HttpStatus.CREATED);

    }

    @GetMapping("/{boardId}/comment")
    ResponseEntity<?> getComment(@PathVariable Long boardId){
        List<Comment> comments = commentRepository.findByBoard(boardRepository.findById(boardId));
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    @PutMapping("/{boardId}/comment/{commentId}")
    ResponseEntity<?> putComment(@RequestBody Comment comment,@PathVariable Long boardId, @PathVariable Long commentId){
        Date date = new Date();
        Board board = boardRepository.findById(boardId).get();
        Comment findcomment = commentRepository.findById(commentId).get();

        findcomment.setContent(comment.getContent());
        findcomment.setTime(format.format(date));

        commentRepository.save(findcomment);
        return new ResponseEntity<>(comment, HttpStatus.OK);


    }

    @DeleteMapping("/{boardId}/comment/commentId}")
    ResponseEntity<?> deleteComment(@PathVariable Long boardId, @PathVariable Long commentId){
        Comment comment = commentRepository.findById(commentId).get();
        boardRepository.deleteById(commentId);
        return new ResponseEntity<>("Delete comment : " + comment.getContent(), HttpStatus.OK );
    }


}