package com.ksu.soccerserver.board;

import com.ksu.soccerserver.account.Account;
import com.ksu.soccerserver.account.AccountRepository;
import com.ksu.soccerserver.account.CurrentAccount;
import com.ksu.soccerserver.board.dto.BoardListRespnse;
import com.ksu.soccerserver.board.dto.BoardRequest;
import com.ksu.soccerserver.board.dto.BoardResponse;
import com.ksu.soccerserver.team.Team;
import com.ksu.soccerserver.team.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.TypeMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import javax.print.attribute.standard.Destination;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/api/boards")
@RequiredArgsConstructor
@RestController
public class BoardController {
    private final AccountRepository accountRepository;
    private final BoardRepository boardRepository;
    private final ModelMapper modelMapper;
    private final TeamRepository teamRepository;


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
    ResponseEntity<?> getBoardList() {
        ModelMapper modelMapper = new ModelMapper();
        //TypeMap typeMap = modelMapper.createTypeMap(boardRepository,BoardListRespnse.class)
         //       .addMapping(boardRepository::getAccount, BoardListRespnse::setAccount);

      /*  PropertyMap<Board, BoardListRespnse> bookMap = new PropertyMap<Board, BoardListRespnse>() {
            protected void configure() {
                map().setName(source.getAccount().getName());
            }
        };



        modelMapper.addMappings(bookMap);
*/
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);


        List<BoardListRespnse> boardListResponse = boardRepository.findAll()
                .stream()
                .map(boardRepository -> modelMapper.map(boardRepository,BoardListRespnse.class))
                .collect(Collectors.toList());

        if(boardListResponse.isEmpty()){
            return new ResponseEntity<>("게시글이 없습니다.", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(boardListResponse, HttpStatus.OK);
    }

    @GetMapping("/{boardId}")
    ResponseEntity<?> getBoardDetail(@PathVariable Long boardId){
        ModelMapper modelMapper = new ModelMapper();
       /* PropertyMap<Board, BoardListRespnse> bookMap = new PropertyMap<Board, BoardListRespnse>() {
            protected void configure() {
                map().setName(source.getAccount().getName());
            }
        };

        modelMapper.addMappings(bookMap);
        */
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);
        Board findBoard = boardRepository.findById(boardId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"존재하지 않는 게시판입니다."));


        BoardResponse response =modelMapper.map(findBoard, BoardResponse.class);

        return new ResponseEntity<>(response,HttpStatus.OK);


    }

    @GetMapping("/myBoard")
    ResponseEntity<?> getAccountsBoard(@CurrentAccount Account currentAccount) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);
        Account account = accountRepository.findById(currentAccount.getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"NO_FOUND_ACCOUNT"));

        List<BoardListRespnse> boardListRespnses = boardRepository.findByAccount(account)
                .stream()
                .map(boardRepository -> modelMapper.map(boardRepository, BoardListRespnse.class))
                .collect(Collectors.toList());

        if(boardListRespnses.isEmpty()){
            return new ResponseEntity<>(currentAccount.getName()+"님이 작성한 개시글이 없습니다.", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(boardListRespnses, HttpStatus.OK);
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
        ModelMapper modelMapper = new ModelMapper();
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
        ModelMapper modelMapper = new ModelMapper();
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