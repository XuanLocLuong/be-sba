package sba301.fe.edu.vn.besba.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sba301.fe.edu.vn.besba.entity.Booking;
import sba301.fe.edu.vn.besba.entity.SeatStatus;
import sba301.fe.edu.vn.besba.repository.BookingRepository;
import sba301.fe.edu.vn.besba.repository.SeatStatusRepository;
import sba301.fe.edu.vn.besba.repository.TicketRepository;
import sba301.fe.edu.vn.besba.repository.VoucherUsageRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CleanupService {
    private final BookingRepository bookingRepository;
    private final SeatStatusRepository seatStatusRepository;
    private final TicketRepository ticketRepository;
    private final VoucherUsageRepository voucherUsageRepository;

    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void cleanupExpiredBookings() {
        LocalDateTime expiryTime = LocalDateTime.now().minusMinutes(10);
        List<Booking> expiredBookings = bookingRepository.findByStatusAndCreatedAtBefore("PENDING", expiryTime);
        log.info("Tìm thấy {} đơn hết hạn", expiredBookings.size());
        for (Booking booking : expiredBookings) {
            try {
                ticketRepository.deleteByBookingId(booking.getId());
                voucherUsageRepository.deleteByBookingId(booking.getId());

                List<SeatStatus> seatStatuses = seatStatusRepository.findByBooking_Id(booking.getId());
                for (SeatStatus ss : seatStatuses) {
                    ss.setStatus("AVAILABLE");
                    ss.setUser(null);
                    ss.setBooking(null);
                    seatStatusRepository.save(ss);
                }

                bookingRepository.delete(booking);
                log.info("Đã xóa đơn hết hạn {}", booking.getId());
            } catch (Exception e) {
                log.error("Lỗi khi xóa đơn {}", booking.getId(), e);
            }
        }
    }
}
