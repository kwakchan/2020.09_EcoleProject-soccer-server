package com.ksu.soccerserver.board;

import com.ksu.soccerserver.account.Account;
import com.ksu.soccerserver.account.AccountRepository;
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
public class BoardController {
    private final AccountRepository accountRepository;
    private final BoardRepository boardRepository;
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @PostMapping("{accountId}")
    ResponseEntity<?> postBoard(@RequestBody Board board, @PathVariable Long accountId){
        Date date = new Date();

        Account account = accountRepository.findById(accountId).get();
        Board saveboard = boardRepository.save(Board.builder()
                .title(board.getTitle())
                .content(board.getContent())
                .time(format.format(date))
                .build());

        saveboard.boardaccount(account);

        boardRepository.save(saveboard);
        return new ResponseEntity<>("Create new board", HttpStatus.CREATED);
    }

    @GetMapping
    ResponseEntity<?> getBoard() {
        List<Board> boards = boardRepository.findAll();
        return new ResponseEntity<>(boards, HttpStatus.OK);
    }

    @GetMapping("/{accountId}")
    ResponseEntity<?> getaccountsBoard(@PathVariable Long accountId) {
        List<Board> boards = boardRepository.findByAccount(accountRepository.findById(accountId));

        return new ResponseEntity<>(boards, HttpStatus.OK);
    }

    @PutMapping("/{boardId}")
    ResponseEntity<?> putBoard(@PathVariable Long boardId, @RequestBody Board board){

        Date date = new Date();
        Board findBoard = boardRepository.findById(boardId).get();


        findBoard.setTitle(board.getTitle());
        findBoard.setContent(board.getContent());
        findBoard.setTime(format.format(date));

        boardRepository.save(findBoard);
        return new ResponseEntity<>(board, HttpStatus.OK);

    }

    @DeleteMapping("{boardId}")
    ResponseEntity<?> deleteBoard(@PathVariable Long boardId){
        Board board = boardRepository.findById(boardId).get();
        boardRepository.deleteById(boardId);
        return new ResponseEntity<>("Delete board title : " + board.getTitle(), HttpStatus.OK);
    }

}