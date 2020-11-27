package com.ksu.soccerserver.comment;

import com.ksu.soccerserver.account.Account;
import com.ksu.soccerserver.account.AccountRepository;
import com.ksu.soccerserver.account.CurrentAccount;
import com.ksu.soccerserver.board.Board;
import com.ksu.soccerserver.board.BoardRepository;
import com.ksu.soccerserver.comment.dto.CommentRequest;
import com.ksu.soccerserver.comment.dto.CommentResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;

@RequestMapping("/api/comments")
@RequiredArgsConstructor
@RestController
public class CommentController {
    private final CommentRepository commentRepository;
    private final AccountRepository accountRepository;
    private final BoardRepository boardRepository;
    private final ModelMapper modelMaapper;

    @PostMapping
    ResponseEntity<?> postComment(@RequestBody CommentRequest commentRequest, @CurrentAccount Account currentAccount) {

        Account account = accountRepository.findById(currentAccount.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"NO_FOUND_ACCOUNT"));

        Board board = boardRepository.findById(commentRequest.getBoardId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"NO_FOUND_BOARD"));

        Comment postComment = commentRequest.toEntity(board, account);
        Comment saveComment = commentRepository.save(postComment);

        CommentResponse response = modelMaapper.map(saveComment, CommentResponse.class);
        response.setName(account.getName());
        response.setImage(account.getImage());

        return new ResponseEntity<>(response,HttpStatus.CREATED);
    }

    @PutMapping("/{commentId}")
    ResponseEntity<?> putComment(@RequestBody CommentRequest commentRequest, @PathVariable Long commentId, @CurrentAccount Account currentAccount){
        Comment findComment = commentRepository.findById(commentId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 댓글입니다."));
        if(!currentAccount.getId().equals(findComment.getAccount().getId())){
            return new ResponseEntity<>("수정권한이 없습니다.", HttpStatus.BAD_REQUEST);
        }else {

            findComment.setContent(commentRequest.getContent());
            findComment.setTime(LocalDateTime.now());

            Comment updatedComment = commentRepository.save(findComment);

            CommentResponse response = modelMaapper.map(updatedComment, CommentResponse.class);
            response.setName(findComment.getAccount().getName());
            response.setImage(findComment.getAccount().getImage());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

    }

    @DeleteMapping("/{commentId}")
    ResponseEntity<?> deleteComment(@PathVariable Long commentId,@CurrentAccount Account currentAccount){
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 댓글입니다."));
        if(!currentAccount.getId().equals(comment.getAccount().getId())){
            return new ResponseEntity<>("삭제권한이 없습니다.",HttpStatus.BAD_REQUEST);
        }else {

            commentRepository.delete(comment);

            CommentResponse response = modelMaapper.map(comment, CommentResponse.class);
            response.setName(comment.getAccount().getName());
            response.setImage(comment.getAccount().getImage());

            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }


}