package sba301.fe.edu.vn.besba.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sba301.fe.edu.vn.besba.entity.Showtime;

import java.time.LocalDateTime;

@Repository
public interface ShowtimeRepository extends JpaRepository<Showtime, Integer> {

    // Kiểm tra xem phòng có bị trùng lịch chiếu không
    @Query("SELECT COUNT(s) > 0 FROM Showtime s WHERE s.room.id = :roomId " +
            "AND s.id != :excludeShowtimeId " +
            "AND s.status != 'CANCELLED' " +
            "AND ((s.startTime <= :endTime AND s.endTime >= :startTime))")
    boolean isRoomBusy(Integer roomId, LocalDateTime startTime, LocalDateTime endTime, Integer excludeShowtimeId);
}