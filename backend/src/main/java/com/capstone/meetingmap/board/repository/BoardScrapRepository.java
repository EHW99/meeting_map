package com.capstone.meetingmap.board.repository;

import com.capstone.meetingmap.board.dto.BoardScrapView;
import com.capstone.meetingmap.board.entity.BoardScrap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BoardScrapRepository extends JpaRepository<BoardScrap, Integer> {
    Optional<BoardScrap> findByUser_UserIdAndBoard_BoardNo(String userId, Integer boardNo);

    @Query("SELECT bs.board.boardNo FROM BoardScrap bs WHERE bs.user.userId = :userId")
    List<Integer> findBoardNosByUserId(@Param("userId") String userId);

    @Query("""
        SELECT new com.capstone.meetingmap.board.dto.BoardScrapView(
            bs.scrapNo, b.boardNo, b.user.userId, b.user.userNick, b.user.userRole.userType, b.user.userRole.userTypeName,
            b.boardTitle, b.boardDescription, b.boardViewCount, b.boardWriteDate, b.boardUpdateDate,
            (SELECT COUNT(l) + 0L FROM BoardLike l WHERE l.board.boardNo = b.boardNo),
            (SELECT COUNT(h) + 0L FROM BoardHate h WHERE h.board.boardNo = b.boardNo),
            b.category.categoryNo, b.category.categoryName,
            (SELECT COUNT(c) + 0L FROM Comment c WHERE c.board.boardNo = b.boardNo),
            b.user.userImg
        )
        FROM BoardScrap bs
        JOIN bs.board b
        WHERE bs.user.userId = :userId
        ORDER BY b.boardUpdateDate DESC
    """)
    List<BoardScrapView> findScrapedBoardViewsByUserIdOrderByBoardUpdateDateDesc(@Param("userId") String userId);
}
