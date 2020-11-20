package com.ksu.soccerserver.board;

import com.ksu.soccerserver.account.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long>{


    List<Board> findByAccount(Account account);

    List<Board> findByTitleContaining(String keyword);

    List<Board> findByBoardtype(String keyword);

    //pagination
    Page<Board> findAllByAccount(Account not_account, Pageable pageable);

    Page<Board> findAllByTitleContaining(String keyword, Pageable pageable);

    Page<Board> findAllByBoardtype(String keyword, Pageable pageable);
}