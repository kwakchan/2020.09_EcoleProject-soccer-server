package com.ksu.soccerserver.board;

import com.ksu.soccerserver.account.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long>{

    List<Board> findByAccount(Account account);

    List<Board> findAllByBoardTypeAndTitleContaining(String boardType, String title);

<<<<<<< HEAD
    List<Board> findAllByTitleContaining(String title);

=======
    List<Board> findByTitleContainingAndBoardType(String title, String boardType);
  
>>>>>>> 982cad2a24175ef23bf24e948444ef59b0b928ea
    //pagination
    Page<Board> findAllByAccount(Account account, Pageable pageable);

    Page<Board> findAllByTitleContaining(String keyword, Pageable pageable);

    Page<Board> findAllByBoardType(String keyword, Pageable pageable);


}