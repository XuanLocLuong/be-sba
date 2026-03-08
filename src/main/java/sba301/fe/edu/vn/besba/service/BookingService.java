package sba301.fe.edu.vn.besba.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sba301.fe.edu.vn.besba.dto.BookingRequest;
import sba301.fe.edu.vn.besba.dto.BookingResponse;
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

    @Transactional
    public BookingResponse createBooking(BookingRequest request) {
        UserPrincipal currentUser = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new CustomException(404, "User not found", null));

        Showtime showtime = showtimeRepository.findById(request.getShowtimeId())
                .orElseThrow(() -> new CustomException(404, "Showtime not found", null));

        // Lấy danh sách SeatStatus của các ghế được chọn
        List<SeatStatus> seatStatuses = seatStatusRepository.findByShowtime_Id(request.getShowtimeId()).stream()
                .filter(ss -> request.getSeatIds().contains(ss.getSeat().getId()))
                .collect(Collectors.toList());

        // Kiểm tra tất cả đều do user này reserve
        for (SeatStatus ss : seatStatuses) {
            if (!"RESERVED".equals(ss.getStatus()) || !ss.getUser().getId().equals(user.getId())) {
                throw new CustomException(400, "Some seats are not reserved by you", null);
            }
        }

        // Tính tổng tiền
        double totalAmount = seatStatuses.stream()
                .mapToDouble(ss -> calculatePrice(ss.getSeat().getSeatType(), showtime.getBasePrice()))
                .sum();

        // Xử lý voucher nếu có
        Voucher voucher = null;
        if (request.getVoucherId() != null) {
            voucher = voucherRepository.findById(request.getVoucherId())
                    .orElseThrow(() -> new CustomException(404, "Voucher not found", null));
            if (voucher.getExpiryDate().before(new java.util.Date())) {
                throw new CustomException(400, "Voucher expired", null);
            }
            double discount = totalAmount * voucher.getDiscountPercent() / 100;
            if (discount > voucher.getMaxDiscountAmount()) {
                discount = voucher.getMaxDiscountAmount();
            }
            totalAmount -= discount;
        }

        // Tạo booking với status PENDING
        Booking booking = Booking.builder()
                .user(user)
                .showtime(showtime)
                .totalAmount(totalAmount)
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .build();
        booking = bookingRepository.save(booking);

        // Gán booking cho từng SeatStatus (vẫn giữ status RESERVED)
        for (SeatStatus ss : seatStatuses) {
            ss.setBooking(booking);
            seatStatusRepository.save(ss);
        }

        // Nếu có voucher, ghi nhận VoucherUsage
        if (voucher != null) {
            VoucherUsage usage = VoucherUsage.builder()
                    .voucher(voucher)
                    .user(user)
                    .booking(booking)
                    .usedAt(LocalDateTime.now())
                    .build();
            voucherUsageRepository.save(usage);
        }

        // Trả về response
        return BookingResponse.builder()
                .bookingId(booking.getId())
                .totalAmount(booking.getTotalAmount())
                .status(booking.getStatus())
                .createdAt(booking.getCreatedAt())
                .tickets(Collections.emptyList())
                .build();
    }

    private double calculatePrice(String seatType, Double basePrice) {
        switch (seatType) {
            case "VIP":
                return basePrice * 1.5;
            case "COUPLE":
                return basePrice * 2.0;
            default:
                return basePrice;
        }
    }

    @Transactional
    public BookingResponse confirmBooking(Integer bookingId) {
        UserPrincipal currentUser = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new CustomException(404, "User not found", null));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new CustomException(404, "Booking not found", null));

        if (!booking.getUser().getId().equals(user.getId())) {
            throw new CustomException(403, "You don't have permission to confirm this booking", null);
        }

        if (!"PENDING".equals(booking.getStatus())) {
            throw new CustomException(400, "Booking already confirmed or cancelled", null);
        }

        // Lấy danh sách SeatStatus thuộc booking này
        List<SeatStatus> seatStatuses = seatStatusRepository.findByBooking_Id(bookingId);

        if (seatStatuses.isEmpty()) {
            throw new CustomException(400, "No seats found for this booking", null);
        }

        // Tạo tickets
        List<Ticket> tickets = new ArrayList<>();
        for (SeatStatus ss : seatStatuses) {
            Ticket ticket = Ticket.builder()
                    .booking(booking)
                    .seat(ss.getSeat())
                    .ticketPrice(calculatePrice(ss.getSeat().getSeatType(), booking.getShowtime().getBasePrice()))
                    .qrCode(UUID.randomUUID().toString())
                    .checkInStatus(false)
                    .build();
            tickets.add(ticketRepository.save(ticket));

            ss.setStatus("BOOKED");
            ss.setUser(null);
            // giữ nguyên booking đã có
            seatStatusRepository.save(ss);
        }

        // Cập nhật booking status thành PAID
        booking.setStatus("PAID");
        bookingRepository.save(booking);

        // Tạo response
        List<TicketResponse> ticketResponses = tickets.stream()
                .map(t -> TicketResponse.builder()
                        .ticketId(t.getId())
                        .seatName(t.getSeat().getRowName() + t.getSeat().getSeatNumber())
                        .price(t.getTicketPrice())
                        .qrCode(t.getQrCode())
                        .build())
                .collect(Collectors.toList());

        return BookingResponse.builder()
                .bookingId(booking.getId())
                .totalAmount(booking.getTotalAmount())
                .status(booking.getStatus())
                .createdAt(booking.getCreatedAt())
                .tickets(ticketResponses)
                .build();
    }

    @Transactional
    public void cancelBooking(Integer bookingId) {
        UserPrincipal currentUser = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new CustomException(404, "User not found", null));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new CustomException(404, "Booking not found", null));

        if (!booking.getUser().getId().equals(user.getId())) {
            throw new CustomException(403, "Bạn không có quyền hủy đơn này", null);
        }

        if (!"PENDING".equals(booking.getStatus())) {
            throw new CustomException(400, "Chỉ có thể hủy đơn đang chờ thanh toán", null);
        }

        // Xóa tickets liên quan
        ticketRepository.deleteByBookingId(bookingId);

        // Xóa voucher usage nếu có
        voucherUsageRepository.deleteByBookingId(bookingId);

        // Lấy danh sách seat_status thuộc booking này
        List<SeatStatus> seatStatuses = seatStatusRepository.findByBooking_Id(bookingId);
        for (SeatStatus ss : seatStatuses) {
            ss.setStatus("AVAILABLE");
            ss.setUser(null);
            ss.setBooking(null);
            seatStatusRepository.save(ss);
        }

        // Xóa booking
        bookingRepository.delete(booking);
    }
}
