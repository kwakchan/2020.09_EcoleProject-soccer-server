package com.ksu.soccerserver.board;

import com.ksu.soccerserver.account.Account;
import com.ksu.soccerserver.account.AccountRepository;
import com.ksu.soccerserver.account.CurrentAccount;
import com.ksu.soccerserver.board.dto.BoardRequest;
import com.ksu.soccerserver.board.dto.BoardResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
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
    private final ModelMapper modelMapper;

    @PostMapping
    ResponseEntity<?> postBoard(@RequestBody BoardRequest boardRequest, @CurrentAccount Account currentAccount){

        Account account = accountRepository.findById(currentAccount.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"NO_FOUND_ACCOUNT"));

        Board postBoard = boardRequest.toEntity(account);

        Board saveBoard = boardRepository.save(postBoard);

        BoardResponse response = modelMapper.map(saveBoard, BoardResponse.class);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    ResponseEntity<?> getBoard() {
        List<Board> boards = boardRepository.findAll();
        if(boards.isEmpty()){
            return new ResponseEntity<>("게시글이 없습니다.", HttpStatus.NOT_FOUND);
        }



        return new ResponseEntity<>(boards, HttpStatus.OK);
    }

    @GetMapping("/myBoard")
    ResponseEntity<?> getAccountsBoard(@CurrentAccount Account currentAccount) {
        Account Account = accountRepository.findById(currentAccount.getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"NO_FOUND_ACCOUNT"));
        List<Board> boards = boardRepository.findByAccount(Account);

        if(boards.isEmpty()){
            return new ResponseEntity<>(currentAccount.getName()+"님이 작성한 개시글이 없습니다.", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(boards, HttpStatus.OK);
    }

    //게시판 keyword포함 제목 검색
    @GetMapping("/search")
    ResponseEntity<?> getSearchBoard(@RequestParam(value = "keyword") String keyword){
        List<Board> boards = boardRepository.findByTitleContaining(keyword);
        if(boards.isEmpty()){
            return new ResponseEntity<>("게시글이 없습니다.", HttpStatus.NOT_FOUND);
        }





        return new ResponseEntity<>(boards, HttpStatus.OK);
    }

    //boardType 게시판 출력
    /*
    @GetMapping("/boardType")
    ResponseEntity<?> getFilteredBoard(@RequestParam(value = "keyword")String keyword){
        List<Board> boards = boardRepository.findByBoardtype(keyword);
        if(boards.isEmpty()){
            return new ResponseEntity<>("게시글이 없습니다.", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(boards, HttpStatus.OK);
    }
    */

    //////////////////////////
    //boardPagination
    @GetMapping("/page")
    ResponseEntity<?> getPaginationBoard(Pageable pageable){
        Page<Board> boardPage = boardRepository.findAll(pageable);

        if(boardPage.isEmpty()) {
            return new ResponseEntity<>("게시글이 없습니다", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(boardPage, HttpStatus.OK);
    }

    @GetMapping("/myBoard/page")
    ResponseEntity<?> getPaginationAccountsBoard(@CurrentAccount Account currentAccount, Pageable pageable) {
        Page<Board> boardPage = boardRepository.findAllByAccount(accountRepository.findById(currentAccount.getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"NOT ACCOUNT")), pageable);
        if(boardPage.isEmpty()){
            return new ResponseEntity<>(currentAccount.getName()+"님이 작성한 개시글이 없습니다.", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(boardPage, HttpStatus.OK);
    }

    @GetMapping("/search/page")
    ResponseEntity<?> getPaginationSearchBoard(@RequestParam(value = "keyword") String keyword, Pageable pageable) {
        Page<Board> boardPage = boardRepository.findAllByTitleContaining(keyword, pageable);
        if (boardPage.isEmpty()) {
            return new ResponseEntity<>("게시글이 없습니다.", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(boardPage,HttpStatus.OK);
    }

    @GetMapping("/boardType/page")
    ResponseEntity<?> getPaginationFilteredBoard(@RequestParam(value = "keyword")String keyword, Pageable pageable){
        Page<Board> boardPage = boardRepository.findAllByBoardType(keyword, pageable);
        if(boardPage.isEmpty()){
            return new ResponseEntity<>("게시글이 없습니다.", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(boardPage,HttpStatus.OK);
    }


    @PutMapping("/{boardId}")
    ResponseEntity<?> putBoard(@PathVariable Long boardId, @RequestBody BoardRequest boardRequest,@CurrentAccount Account currentAccount){
        Board findBoard = boardRepository.findById(boardId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 게시판입니다."));

        if(!currentAccount.getId().equals(findBoard.getAccount().getId())){
            return new ResponseEntity<>("수정권한이 없습니다.", HttpStatus.BAD_REQUEST);
        }else {


            findBoard.setTitle(boardRequest.getTitle());
            findBoard.setContent(boardRequest.getContent());
            findBoard.setTime(LocalDateTime.now());
            findBoard.setBoardtype(boardRequest.getBoardType());

            boardRepository.save(findBoard);
            BoardResponse response = modelMapper.map(findBoard, BoardResponse.class);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

    }

    @DeleteMapping("/{boardId}")
    ResponseEntity<?> deleteBoard(@PathVariable Long boardId, @CurrentAccount Account currentAccount){
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 게시판입니다."));
        if(!currentAccount.getId().equals(board.getAccount().getId())){
            return new ResponseEntity<>("삭제권한이 없습니다.", HttpStatus.BAD_REQUEST);
        }else {
            boardRepository.delete(board);
            BoardResponse response = modelMapper.map(board, BoardResponse.class);
            return new ResponseEntity(response, HttpStatus.OK);
        }
    }

}