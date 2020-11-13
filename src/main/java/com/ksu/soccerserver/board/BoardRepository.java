package com.ksu.soccerserver.board;

import com.ksu.soccerserver.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {
    List<Board> findByAccount(Optional<Account> account);
}