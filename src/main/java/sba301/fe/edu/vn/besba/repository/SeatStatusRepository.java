package sba301.fe.edu.vn.besba.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sba301.fe.edu.vn.besba.entity.SeatStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeatStatusRepository extends JpaRepository<SeatStatus, Integer> {
    List<SeatStatus> findByShowtime_Id(Integer showtimeId);

    Optional<SeatStatus> findByShowtime_IdAndSeat_Id(Integer showtimeId, Integer seatId);
  
    List<SeatStatus> findByBooking_Id(Integer bookingId);

    List<SeatStatus> findByShowtimeIdAndSeatIdIn(Integer showtimeId, List<Integer> seatIds);
}