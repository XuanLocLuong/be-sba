package sba301.fe.edu.vn.besba.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sba301.fe.edu.vn.besba.entity.Booking;
import sba301.fe.edu.vn.besba.entity.SeatStatus;
import sba301.fe.edu.vn.besba.entity.Ticket;
import sba301.fe.edu.vn.besba.exception.CustomException;
import sba301.fe.edu.vn.besba.repository.BookingRepository;
import sba301.fe.edu.vn.besba.repository.SeatStatusRepository;
import sba301.fe.edu.vn.besba.repository.TicketRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketService {
    private final TicketRepository ticketRepository;
    private final BookingRepository bookingRepository;
    private final SeatStatusRepository seatStatusRepository;

    @Transactional
    public String checkInTicket(String qrCode) {
        Ticket ticket = ticketRepository.findByQrCode(qrCode)
                .orElseThrow(() -> new CustomException(404, "Vé không tồn tại hoặc mã QR sai", HttpStatus.NOT_FOUND));

        if (Boolean.TRUE.equals(ticket.getCheckInStatus())) {
            throw new CustomException(400, "Vé này đã được sử dụng (Checked-in) trước đó!", HttpStatus.BAD_REQUEST);
        }

        if (Boolean.TRUE.equals(ticket.getIsCancelled())) {
            throw new CustomException(400, "Vé này đã bị hủy, không thể sử dụng!", HttpStatus.BAD_REQUEST);
        }

        ticket.setCheckInStatus(true);
        ticket.setCheckInTime(LocalDateTime.now());
        ticketRepository.save(ticket);

        Booking booking = ticket.getBooking();

        boolean isAllCheckedIn = booking.getTickets().stream()
                .filter(t -> !Boolean.TRUE.equals(t.getIsCancelled()))
                .allMatch(t -> t.getCheckInStatus() || t.getId().equals(ticket.getId()));

        if (isAllCheckedIn) {
            booking.setStatus("COMPLETED");
            bookingRepository.save(booking);
        }


        return "Check-in thành công cho ghế: " + ticket.getSeat().getRowName() + ticket.getSeat().getSeatNumber();
    }

    @Transactional
    public void cancelSingleTicket(String qrCode) {
        Ticket ticket = ticketRepository.findByQrCode(qrCode)
                .orElseThrow(() -> new CustomException(404, "Không tìm thấy vé với mã QR này!", HttpStatus.NOT_FOUND));

        if (ticket.getCheckInStatus()) {
            throw new CustomException(400, "Vé này đã được soát để vào rạp, không thể hủy!", HttpStatus.BAD_REQUEST);
        }

        if (Boolean.TRUE.equals(ticket.getIsCancelled())) {
            throw new CustomException(400, "Vé này đã bị hủy từ trước!", HttpStatus.BAD_REQUEST);
        }

        Booking booking = ticket.getBooking();

        List<SeatStatus> seatStatuses = seatStatusRepository.findByBooking_Id(booking.getId());
        for (SeatStatus ss : seatStatuses) {
            if (ss.getSeat().getId().equals(ticket.getSeat().getId())) {
                ss.setStatus("AVAILABLE");
                ss.setUser(null);
                ss.setBooking(null);
                seatStatusRepository.save(ss);
                break;
            }
        }

        double newTotal = booking.getTotalAmount() - ticket.getTicketPrice();
        booking.setTotalAmount(newTotal < 0 ? 0 : newTotal);

        ticket.setIsCancelled(true);
        ticketRepository.save(ticket); // Lưu lại lịch sử

        boolean allCancelled = booking.getTickets().stream()
                .allMatch(t -> Boolean.TRUE.equals(t.getIsCancelled()));
        if (allCancelled) {
            booking.setStatus("CANCELLED");
        }

        bookingRepository.save(booking);
    }
}