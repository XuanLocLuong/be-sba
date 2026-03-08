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
}
