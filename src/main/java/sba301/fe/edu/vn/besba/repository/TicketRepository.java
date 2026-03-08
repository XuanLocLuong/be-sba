package sba301.fe.edu.vn.besba.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sba301.fe.edu.vn.besba.entity.Ticket;

import java.util.Collection;
import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Integer> {
    List<Ticket> findByBooking_Id(Integer bookingId);

    void deleteByBookingId(Integer bookingId);
}
