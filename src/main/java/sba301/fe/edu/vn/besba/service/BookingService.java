package sba301.fe.edu.vn.besba.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sba301.fe.edu.vn.besba.dto.BookingRequest;
import sba301.fe.edu.vn.besba.dto.response.BookingResponse;
import sba301.fe.edu.vn.besba.dto.TicketResponse;
import sba301.fe.edu.vn.besba.entity.*;
import sba301.fe.edu.vn.besba.exception.CustomException;
import sba301.fe.edu.vn.besba.repository.*;
import sba301.fe.edu.vn.besba.security.UserPrincipal;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final ShowtimeRepository showtimeRepository;
    private final SeatStatusRepository seatStatusRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final VoucherRepository voucherRepository;
    private final VoucherUsageRepository voucherUsageRepository;
  
    public List<BookingResponse> getAllBookings() {
        return bookingRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(BookingResponse::fromEntity)
                .toList();
    }

    // Tạo đơn đặt vé
    @Transactional
    public BookingResponse createBooking(BookingRequest request) {
        UserPrincipal currentUser = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new CustomException(404, "User not found", HttpStatus.NOT_FOUND));

        Showtime showtime = showtimeRepository.findById(request.getShowtimeId())
                .orElseThrow(() -> new CustomException(404, "Showtime not found", HttpStatus.NOT_FOUND));

        List<SeatStatus> seatStatuses = seatStatusRepository.findByShowtime_Id(request.getShowtimeId()).stream()
                .filter(ss -> request.getSeatIds().contains(ss.getSeat().getId()))
                .collect(Collectors.toList());

        for (SeatStatus ss : seatStatuses) {
            if (!"RESERVED".equals(ss.getStatus()) || !ss.getUser().getId().equals(user.getId())) {
                throw new CustomException(400, "Some seats are not reserved by you", HttpStatus.BAD_REQUEST);
            }
        }

        double totalAmount = seatStatuses.stream()
                .mapToDouble(ss -> calculatePrice(ss.getSeat().getSeatType(), showtime.getBasePrice()))
                .sum();

        // Xử lý Voucher
        Voucher voucher = null;
        if (request.getVoucherId() != null) {
            voucher = voucherRepository.findById(request.getVoucherId())
                    .orElseThrow(() -> new CustomException(404, "Voucher not found", HttpStatus.NOT_FOUND));
            if (voucher.getExpiryDate().before(new java.util.Date())) {
                throw new CustomException(400, "Voucher expired", HttpStatus.BAD_REQUEST);
            }
            double discount = Math.min(totalAmount * voucher.getDiscountPercent() / 100, voucher.getMaxDiscountAmount());
            totalAmount -= discount;
        }

        Booking booking = bookingRepository.save(Booking.builder()
                .user(user).showtime(showtime).totalAmount(totalAmount)
                .status("PENDING").createdAt(LocalDateTime.now()).build());

        for (SeatStatus ss : seatStatuses) {
            ss.setBooking(booking);
            seatStatusRepository.save(ss);
        }

        if (voucher != null) {
            voucherUsageRepository.save(VoucherUsage.builder()
                    .voucher(voucher).user(user).booking(booking).usedAt(LocalDateTime.now()).build());
        }

        return BookingResponse.builder()
                .bookingId(booking.getId()).totalAmount(booking.getTotalAmount())
                .status(booking.getStatus()).createdAt(booking.getCreatedAt())
                .tickets(Collections.emptyList()).build();
    }

    // Xác nhận đơn sau khi thanh toán 
    @Transactional
    public BookingResponse confirmBooking(Integer bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new CustomException(404, "Booking not found", HttpStatus.NOT_FOUND));

        if (!"PENDING".equals(booking.getStatus())) {
            throw new CustomException(400, "Trạng thái đơn không hợp lệ", HttpStatus.BAD_REQUEST);
        }

        List<SeatStatus> seatStatuses = seatStatusRepository.findByBooking_Id(bookingId);
        List<Ticket> tickets = new ArrayList<>();

        for (SeatStatus ss : seatStatuses) {
            Ticket ticket = ticketRepository.save(Ticket.builder()
                    .booking(booking).seat(ss.getSeat())
                    .ticketPrice(calculatePrice(ss.getSeat().getSeatType(), booking.getShowtime().getBasePrice()))
                    .qrCode(UUID.randomUUID().toString()).checkInStatus(false).build());
            tickets.add(ticket);

            ss.setStatus("BOOKED");
            ss.setUser(null);
            seatStatusRepository.save(ss);
        }

        booking.setStatus("PAID");
        bookingRepository.save(booking);

        return BookingResponse.builder()
                .bookingId(booking.getId()).status(booking.getStatus())
                .tickets(tickets.stream().map(t -> TicketResponse.builder()
                        .ticketId(t.getId()).seatName(t.getSeat().getRowName() + t.getSeat().getSeatNumber())
                        .price(t.getTicketPrice()).qrCode(t.getQrCode()).build()).collect(Collectors.toList()))
                .build();
    }

    // User hủy đơn PENDING
    @Transactional
    public void cancelBooking(Integer bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new CustomException(404, "Booking not found", HttpStatus.NOT_FOUND));

        if (!"PENDING".equals(booking.getStatus())) {
            throw new CustomException(400, "Chỉ có thể hủy đơn đang chờ thanh toán", HttpStatus.BAD_REQUEST);
        }

        ticketRepository.deleteByBookingId(bookingId);
        voucherUsageRepository.deleteByBookingId(bookingId);
        
        List<SeatStatus> seatStatuses = seatStatusRepository.findByBooking_Id(bookingId);
        for (SeatStatus ss : seatStatuses) {
            ss.setStatus("AVAILABLE");
            ss.setUser(null);
            ss.setBooking(null);
            seatStatusRepository.save(ss);
        }
        bookingRepository.delete(booking);
    }

    // Admin hủy đơn 
    @Transactional
    public void cancelBookingByAdmin(Integer bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new CustomException(404, "Không tìm thấy đơn!", HttpStatus.NOT_FOUND));

        if ("CANCELLED".equals(booking.getStatus())) {
            throw new CustomException(400, "Đơn đã được hủy trước đó!", HttpStatus.BAD_REQUEST);
        }

        booking.setStatus("CANCELLED");
        bookingRepository.save(booking);

        List<Integer> seatIds = booking.getTickets().stream().map(t -> t.getSeat().getId()).collect(Collectors.toList());
        if (!seatIds.isEmpty()) {
            List<SeatStatus> seatStatuses = seatStatusRepository.findByShowtimeIdAndSeatIdIn(booking.getShowtime().getId(), seatIds);
            for (SeatStatus ss : seatStatuses) {
                ss.setStatus("AVAILABLE");
                ss.setUser(null);
            }
            seatStatusRepository.saveAll(seatStatuses);
        }
    }

    private double calculatePrice(String seatType, Double basePrice) {
        return switch (seatType) {
            case "VIP" -> basePrice * 1.5;
            case "COUPLE" -> basePrice * 2.0;
            default -> basePrice;
        };
    }
}