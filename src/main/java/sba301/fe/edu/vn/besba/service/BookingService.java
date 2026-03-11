package sba301.fe.edu.vn.besba.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import sba301.fe.edu.vn.besba.dto.response.BookingResponse;
import sba301.fe.edu.vn.besba.entity.Booking;
import sba301.fe.edu.vn.besba.entity.Seat;
import sba301.fe.edu.vn.besba.entity.SeatStatus;
import sba301.fe.edu.vn.besba.entity.Ticket;
import sba301.fe.edu.vn.besba.exception.CustomException;
import sba301.fe.edu.vn.besba.repository.BookingRepository;
import sba301.fe.edu.vn.besba.repository.SeatStatusRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final SeatStatusRepository seatStatusRepository;

    public List<BookingResponse> getAllBookings() {
        return bookingRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(BookingResponse::fromEntity)
                .toList();
    }

    @Transactional
    public void cancelBooking(Integer bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new CustomException(404, "Không tìm thấy đơn đặt vé này!", HttpStatus.NOT_FOUND));

        if ("CANCELLED".equals(booking.getStatus())) {
            throw new CustomException(400, "Đơn vé này đã được hủy trước đó!", HttpStatus.BAD_REQUEST);
        }
        booking.setStatus("CANCELLED");
        bookingRepository.save(booking);

        List<Integer> seatIds = booking.getTickets().stream()
                .map(Ticket::getSeat)
                .map(Seat::getId)
                .collect(Collectors.toList());

        if (!seatIds.isEmpty()) {
            List<SeatStatus> seatStatuses = seatStatusRepository
                    .findByShowtimeIdAndSeatIdIn(booking.getShowtime().getId(), seatIds);

            for (SeatStatus seatStatus : seatStatuses) {
                seatStatus.setStatus("AVAILABLE");
                seatStatus.setUser(null);
            }
            seatStatusRepository.saveAll(seatStatuses);
        }
}
}