package sba301.fe.edu.vn.besba.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sba301.fe.edu.vn.besba.entity.SeatStatus;

import java.util.List;

@Repository
public interface SeatStatusRepository extends JpaRepository<SeatStatus, Integer> {
    List<SeatStatus> findByShowtimeIdAndSeatIdIn(Integer showtimeId, List<Integer> seatIds);
}