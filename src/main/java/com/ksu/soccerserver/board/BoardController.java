package com.ksu.soccerserver.board;

import com.ksu.soccerserver.account.Account;
import com.ksu.soccerserver.account.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import java.time.LocalDateTime;
import java.util.List;

@RequestMapping("/api/boards")
@RequiredArgsConstructor
@RestController
public class BoardController {
    private final AccountRepository accountRepository;
    private final BoardRepository boardRepository;

    @PostMapping("/{accountId}")
    ResponseEntity<?> postBoard(@RequestBody Board board, @PathVariable Long accountId){

        Account account = accountRepository.findById(accountId).get();
        Board saveboard = boardRepository.save(Board.builder()
                .title(board.getTitle())
                .content(board.getContent())
                .createdAt(LocalDateTime.now())
                .boardtype(board.getBoardtype())
                .build());

        saveboard.boardaccount(account);

        boardRepository.save(saveboard);
        return new ResponseEntity<>("Create new board", HttpStatus.CREATED);
    }

    @GetMapping
    ResponseEntity<?> getBoard() {
        List<Board> boards = boardRepository.findAll();
        if(boards.isEmpty()){
            return new ResponseEntity<>("게시글이 없습니다.", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(boards, HttpStatus.OK);
    }

    @GetMapping("/{accountId}")
    ResponseEntity<?> getaccountsBoard(@PathVariable Long accountId) {
        List<Board> boards = boardRepository.findByAccount(accountRepository.findById(accountId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No_Found_Account")));

        if(boards.isEmpty()){
            return new ResponseEntity<>(accountRepository.findById(accountId).get().getName()+"님이 작성한 개시글이 없습니다.", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(boards, HttpStatus.OK);
    }

    //게시판 keyword포함 제목 검색
    @GetMapping("/search")
    ResponseEntity<?> getsearchBoard(@RequestParam(value = "keyword") String keyword){
        List<Board> boards = boardRepository.findByTitleContaining(keyword);
        if(boards.isEmpty()){
            return new ResponseEntity<>("게시글이 없습니다.", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(boards, HttpStatus.OK);
    }

    //boardType 게시판 출력
    @GetMapping("/boardType")
    ResponseEntity<?> getfilterdBoard(@RequestParam(value = "keyword")String keyword){
        List<Board> boards = boardRepository.findByBoardtype(keyword);
        if(boards.isEmpty()){
            return new ResponseEntity<>("게시글이 없습니다.", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(boards, HttpStatus.OK);
    }

    //////////////////////////
    //boardPagination
    @GetMapping("/page")
    ResponseEntity<?> getpaginationBoard(Pageable pageable){
        Page<Board> boardPage = boardRepository.findAll(pageable);
        if(boardPage.isEmpty()) {
            return new ResponseEntity<>("게시글이 없습니다", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(boardPage, HttpStatus.OK);
    }

    @GetMapping("/{accountId}/page")
    ResponseEntity<?> getpaginationaccountsBoard(@PathVariable Long accountId, Pageable pageable) {
        Page<Board> boardPage = boardRepository.findAllByAccount(accountRepository.findById(accountId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"NOT ACCOUNT")), pageable);
        if(boardPage.isEmpty()){
            return new ResponseEntity<>(accountRepository.findById(accountId).get().getName()+"님이 작성한 개시글이 없습니다.", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(boardPage, HttpStatus.OK);
    }

    @GetMapping("/search/page")
    ResponseEntity<?> getpaginationsearchBoard(@RequestParam(value = "keyword") String keyword, Pageable pageable) {
        Page<Board> boardPage = boardRepository.findAllByTitleContaining(keyword, pageable);
        if (boardPage.isEmpty()) {
            return new ResponseEntity<>("게시글이 없습니다.", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(boardPage,HttpStatus.OK);
    }

    @GetMapping("/boardType/page")
    ResponseEntity<?> getpaginationfilterdBoard(@RequestParam(value = "keyword")String keyword, Pageable pageable){
        Page<Board> boardPage = boardRepository.findAllByBoardtype(keyword, pageable);
        if(boardPage.isEmpty()){
            return new ResponseEntity<>("게시글이 없습니다.", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(boardPage,HttpStatus.OK);
    }




    @PutMapping("/{boardId}")
    ResponseEntity<?> putBoard(@PathVariable Long boardId, @RequestBody Board board){
        Board findBoard = boardRepository.findById(boardId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 게시판입니다."));


        findBoard.setTitle(board.getTitle());
        findBoard.setContent(board.getContent());
        findBoard.setTime(LocalDateTime.now());
        findBoard.setBoardtype(board.getBoardtype());

        boardRepository.save(findBoard);
        return new ResponseEntity<>(findBoard, HttpStatus.OK);

    }

    @DeleteMapping("/{boardId}")
    ResponseEntity<?> deleteBoard(@PathVariable Long boardId){
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 게시판입니다."));

        boardRepository.delete(board);
        return new ResponseEntity<>("Delete board title : " + board.getTitle(), HttpStatus.OK);
    }

}