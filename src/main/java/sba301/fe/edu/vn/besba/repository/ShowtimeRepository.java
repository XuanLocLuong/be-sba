package sba301.fe.edu.vn.besba.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sba301.fe.edu.vn.besba.entity.Showtime;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ShowtimeRepository extends JpaRepository<Showtime, Integer> {
    @Query("SELECT s FROM Showtime s WHERE s.movie.id = :movieId AND s.startTime >= :now ORDER BY s.startTime")
    List<Showtime> findCurrentShowtimeByMovieId(
            @Param("movieId") Integer movieId, @Param("now") LocalDateTime now
    );

    @Query("SELECT COUNT(ss) FROM SeatStatus ss WHERE ss.showtime.id = :showtimeId AND ss.status = 'AVAILABLE'")
    int countAvailableSeatsByShowtimeId(@Param("showtimeId") Integer showtimeId);

    @Query("SELECT COUNT(s) > 0 FROM Showtime s WHERE s.room.id = :roomId " +
            "AND s.id != :excludeShowtimeId " +
            "AND s.status != 'CANCELLED' " +
            "AND ((s.startTime <= :endTime AND s.endTime >= :startTime))")
    boolean isRoomBusy(
            @Param("roomId") Integer roomId, 
            @Param("startTime") LocalDateTime startTime, 
            @Param("endTime") LocalDateTime endTime, 
            @Param("excludeShowtimeId") Integer excludeShowtimeId
    );
}