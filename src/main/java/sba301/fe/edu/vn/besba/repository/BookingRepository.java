package sba301.fe.edu.vn.besba.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sba301.fe.edu.vn.besba.entity.Booking;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {

    List<Booking> findByStatusAndCreatedAtBefore(String status, LocalDateTime time);
    List<Booking> findAllByOrderByCreatedAtDesc();
    long countByStatusNot(String status);

    List<Booking> findAllByCreatedAtAfterAndStatusIn(LocalDateTime dateTime, List<String> statuses);

    // Truy vấn doanh thu theo từng phim
    @Query("SELECT b.showtime.movie.title, SUM(b.totalAmount) FROM Booking b " +
            "WHERE b.status IN ('PAID', 'COMPLETED') " +
            "GROUP BY b.showtime.movie.title")
    List<Object[]> getRevenueByMovie();
}