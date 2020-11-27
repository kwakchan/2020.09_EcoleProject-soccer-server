package com.ksu.soccerserver.board;

import com.ksu.soccerserver.account.Account;
import com.ksu.soccerserver.account.AccountRepository;
import com.ksu.soccerserver.account.CurrentAccount;
import com.ksu.soccerserver.board.dto.BoardListResponse;
import com.ksu.soccerserver.board.dto.BoardRequest;
import com.ksu.soccerserver.board.dto.BoardDetailResponse;
import com.ksu.soccerserver.comment.Comment;
import com.ksu.soccerserver.comment.CommentRepository;
import com.ksu.soccerserver.comment.dto.CommentResponse;
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
import java.util.stream.Collectors;

@RequestMapping("/api/boards")
@RequiredArgsConstructor
@RestController
public class BoardController {

    private final AccountRepository accountRepository;
    private final BoardRepository boardRepository;
    private final ModelMapper modelMapper;
    private final CommentRepository commentRepository;

    @PostMapping
    ResponseEntity<?> postBoard(@RequestBody BoardRequest boardRequest, @CurrentAccount Account currentAccount) {
        Account account = accountRepository.findById(currentAccount.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "NO_FOUND_ACCOUNT"));

        Board postBoard = boardRequest.toEntity(account);
        Board saveBoard = boardRepository.save(postBoard);
        BoardDetailResponse response = modelMapper.map(saveBoard, BoardDetailResponse.class);
        response.setName(account.getName());
        response.setImage(account.getImage());

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    ResponseEntity<?> getBoardList() {
        List<Board> boards = boardRepository.findAll();
        List<BoardListResponse> boardListResponse = boardRepository.findAll()
                .stream()
                .map(boardRepository -> modelMapper.map(boardRepository, BoardListResponse.class))
                .collect(Collectors.toList());

        if (boardListResponse.isEmpty()) {
            return new ResponseEntity<>("게시글이 없습니다.", HttpStatus.NOT_FOUND);
        }

        for (int i = 0; i < boardListResponse.size(); i++) {
            boardListResponse.get(i).setName(boards.get(i).getAccount().getName());
        }

        return new ResponseEntity<>(boardListResponse, HttpStatus.OK);
    }

    @GetMapping("/{boardId}")
    ResponseEntity<?> getBoardDetail(@PathVariable Long boardId) {


        Board findBoard = boardRepository.findById(boardId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 게시판입니다."));
        List<Comment> comment = commentRepository.findByBoard(findBoard);
        List<CommentResponse> commentResponses = commentRepository.findByBoard(findBoard)
                .stream()
                .map(commentRepository -> modelMapper.map(commentRepository, CommentResponse.class))
                .collect(Collectors.toList());

        for (int i=0; i< commentResponses.size(); i++) {
            commentResponses.get(i).setName(comment.get(i).getAccount().getName());
            commentResponses.get(i).setImage(comment.get(i).getAccount().getImage());
        }

        BoardDetailResponse response = modelMapper.map(findBoard, BoardDetailResponse.class);
        response.setName(findBoard.getAccount().getName());
        response.setImage(findBoard.getAccount().getImage());

        for (int i=0;i<comment.size(); i++) {
            response.addComment(commentResponses.get(i));
        }
        //response.setComment(modelMapper.map(comment,CommentResponse.class));

        return new ResponseEntity<>(response,HttpStatus.OK);


    }

    @GetMapping("/myBoard")
    ResponseEntity<?> getAccountsBoard(@CurrentAccount Account currentAccount) {
        Account account = accountRepository.findById(currentAccount.getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"NO_FOUND_ACCOUNT"));

        List<Board> boards = boardRepository.findByAccount(account);
        List<BoardListResponse> boardListResponses = boardRepository.findByAccount(account)
                .stream()
                .map(boardRepository -> modelMapper.map(boardRepository, BoardListResponse.class))
                .collect(Collectors.toList());

        if(boardListResponses.isEmpty()){
            return new ResponseEntity<>(currentAccount.getName()+"님이 작성한 개시글이 없습니다.", HttpStatus.NOT_FOUND);
        }

        for(int i=0; i<boardListResponses.size();i++){
            boardListResponses.get(i).setName(boards.get(i).getAccount().getName());
        }

        return new ResponseEntity<>(boardListResponses, HttpStatus.OK);
    }

    //게시판 keyword포함 제목 검색
    @GetMapping("/search")
    ResponseEntity<?> getSearchBoard(@RequestParam(value = "keyword") String keyword){
        List<Board> boards = boardRepository.findByTitleContaining(keyword);
        List<BoardListResponse> boardListResponses = boardRepository.findByTitleContaining(keyword)
                .stream()
                .map(boardRepository -> modelMapper.map(boardRepository, BoardListResponse.class))
                .collect(Collectors.toList());

        if(boardListResponses.isEmpty()){
            return new ResponseEntity<>(keyword + " 제목의 게시글이 없습니다.", HttpStatus.NOT_FOUND);
        }

        for(int i=0; i<boardListResponses.size();i++){
            boardListResponses.get(i).setName(boards.get(i).getAccount().getName());
        }

        return new ResponseEntity<>(boardListResponses, HttpStatus.OK);
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

    @GetMapping("/page")
    ResponseEntity<?> getPaginationBoard(Pageable pageable){
        Page<BoardListResponse> boardListResponses = boardRepository.findAll(pageable)
                .map(boardRepository -> modelMapper.map(boardRepository, BoardListResponse.class));

        if(boardListResponses.isEmpty()) {
            return new ResponseEntity<>("게시글이 없습니다", HttpStatus.NOT_FOUND);
        }


        return new ResponseEntity<>(boardListResponses, HttpStatus.OK);
    }

    @GetMapping("/myBoard/page")
    ResponseEntity<?> getPaginationAccountsBoard(@CurrentAccount Account currentAccount, Pageable pageable) {
        Page<BoardListResponse> boardListResponses = boardRepository.findAllByAccount(accountRepository.findById(currentAccount.getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"NOT ACCOUNT")), pageable)
                .map(boardRepository -> modelMapper.map(boardRepository, BoardListResponse.class));

        if(boardListResponses.isEmpty()){
            return new ResponseEntity<>(currentAccount.getName()+"님이 작성한 개시글이 없습니다.", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(boardListResponses, HttpStatus.OK);
    }

    @GetMapping("/search/page")
    ResponseEntity<?> getPaginationSearchBoard(@RequestParam(value = "keyword") String keyword, Pageable pageable) {
        Page<BoardListResponse> boardListResponses = boardRepository.findAllByTitleContaining(keyword, pageable)
                .map(boardRepository -> modelMapper.map(boardRepository, BoardListResponse.class));

        if (boardListResponses.isEmpty()) {
            return new ResponseEntity<>(keyword + " 제목의 게시글이 없습니다.", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(boardListResponses,HttpStatus.OK);
    }

    @GetMapping("/boardType/page")
    ResponseEntity<?> getPaginationFilteredBoard(@RequestParam(value = "keyword")String keyword, Pageable pageable){
        Page<BoardListResponse> boardListResponses = boardRepository.findAllByBoardType(keyword, pageable)
                .map(boardRepository -> modelMapper.map(boardRepository, BoardListResponse.class));
        if(boardListResponses.isEmpty()){
            return new ResponseEntity<>(keyword + " 카테고리 게시글이 없습니다.", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(boardListResponses,HttpStatus.OK);
    }


    @PutMapping("/{boardId}")
    ResponseEntity<?> putBoard(@PathVariable Long boardId, @RequestBody BoardRequest boardRequest,@CurrentAccount Account currentAccount){
        Board findBoard = boardRepository.findById(boardId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 게시판입니다."));

        List<Comment> comment = commentRepository.findByBoard(findBoard);
        List<CommentResponse> commentResponses = commentRepository.findByBoard(findBoard)
                .stream()
                .map(commentRepository -> modelMapper.map(commentRepository, CommentResponse.class))
                .collect(Collectors.toList());

        for (int i=0; i< commentResponses.size(); i++) {
            commentResponses.get(i).setName(comment.get(i).getAccount().getName());
            commentResponses.get(i).setImage(comment.get(i).getAccount().getImage());
        }

        if(!currentAccount.getId().equals(findBoard.getAccount().getId())){
            return new ResponseEntity<>("수정권한이 없습니다.", HttpStatus.BAD_REQUEST);
        }else {
            findBoard.setTitle(boardRequest.getTitle());
            findBoard.setContent(boardRequest.getContent());
            findBoard.setTime(LocalDateTime.now());
            findBoard.setBoardtype(boardRequest.getBoardType());
            boardRepository.save(findBoard);

            BoardDetailResponse response = modelMapper.map(findBoard, BoardDetailResponse.class);
            response.setName(findBoard.getAccount().getName());
            response.setImage(findBoard.getAccount().getImage());

            for (int i=0;i<comment.size(); i++) {
                response.addComment(commentResponses.get(i));
            }

            return new ResponseEntity<>(response, HttpStatus.OK);
        }

    }

    @DeleteMapping("/{boardId}")
    ResponseEntity<?> deleteBoard(@PathVariable Long boardId, @CurrentAccount Account currentAccount){
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 게시판입니다."));
        List<Comment> comment = commentRepository.findByBoard(board);

        if(!currentAccount.getId().equals(board.getAccount().getId())){
            return new ResponseEntity<>("삭제권한이 없습니다.", HttpStatus.BAD_REQUEST);
        }
        else {
            if(!comment.isEmpty())
            {
                for (int i=0; i<comment.size(); i++) {
                    commentRepository.delete(comment.get(i));
                }
            }
            boardRepository.delete(board);
            BoardDetailResponse response = modelMapper.map(board, BoardDetailResponse.class);
            response.setName(board.getAccount().getName());
            response.setImage(board.getAccount().getImage());

            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }
}