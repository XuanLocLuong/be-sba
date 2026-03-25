package sba301.fe.edu.vn.besba.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sba301.fe.edu.vn.besba.dto.BookingRequest;
import sba301.fe.edu.vn.besba.dto.TicketResponse;
import sba301.fe.edu.vn.besba.dto.response.BookingResponseStaff;
import sba301.fe.edu.vn.besba.dto.response.BookingResponse;
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

    public List<BookingResponseStaff> getAllBookings() {
        return bookingRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(BookingResponseStaff::fromEntity)
                .toList();
    }

    @Transactional
    public sba301.fe.edu.vn.besba.dto.BookingResponse createBooking(BookingRequest request) {
        UserPrincipal currentUser = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new CustomException(404, "Không tìm thấy người dùng", HttpStatus.NOT_FOUND));

        Showtime showtime = showtimeRepository.findById(request.getShowtimeId())
                .orElseThrow(() -> new CustomException(404, "Không tìm thấy suất chiếu", HttpStatus.NOT_FOUND));

        List<SeatStatus> seatStatuses = seatStatusRepository.findByShowtime_Id(request.getShowtimeId()).stream()
                .filter(ss -> request.getSeatIds().contains(ss.getSeat().getId()))
                .collect(Collectors.toList());

        if (seatStatuses.size() != request.getSeatIds().size()) {
            throw new CustomException(400, "Một số ghế không tồn tại trong suất chiếu này", HttpStatus.BAD_REQUEST);
        }

        for (SeatStatus seatStatus : seatStatuses) {
            System.out.println("SeatStatus: " + seatStatus.toString());
        }

        for (SeatStatus ss : seatStatuses) {
            boolean isAvailable = "AVAILABLE".equals(ss.getStatus());
            boolean isReservedByCurrentUser = "RESERVED".equals(ss.getStatus()) && ss.getUser() != null && ss.getUser().getId().equals(user.getId());

            if (!isAvailable && !isReservedByCurrentUser) {
                throw new CustomException(400, "Ghế " + ss.getSeat().getRowName() + ss.getSeat().getSeatNumber() + " không khả dụng hoặc đã bị giữ bởi người khác!", HttpStatus.BAD_REQUEST);
            }
        }

        // Tính tổng tiền dựa trên loại ghế
        double totalAmount = seatStatuses.stream()
                .mapToDouble(ss -> calculatePrice(ss.getSeat().getSeatType(), showtime.getBasePrice()))
                .sum();

        // Xử lý Voucher
        Voucher voucher = null;
        if (request.getVoucherId() != null) {
            voucher = voucherRepository.findById(request.getVoucherId())
                    .orElseThrow(() -> new CustomException(404, "Không tìm thấy Voucher", HttpStatus.NOT_FOUND));


            if (voucher.getExpiryDate().before(new java.util.Date())) {
                throw new CustomException(400, "Voucher đã hết hạn", HttpStatus.BAD_REQUEST);
            }

            if (totalAmount < voucher.getMinOrderValue()) {
                throw new CustomException(400, "Đơn hàng chưa đạt giá trị tối thiểu để dùng voucher", HttpStatus.BAD_REQUEST);
            }

            double discount = Math.min(totalAmount * voucher.getDiscountPercent() / 100, voucher.getMaxDiscountAmount());
            totalAmount -= discount;
        }

        Booking booking = bookingRepository.save(Booking.builder()
                .user(user)
                .showtime(showtime)
                .totalAmount(totalAmount)
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .build());

        for (SeatStatus ss : seatStatuses) {
            ss.setStatus("RESERVED");
            ss.setUser(user);
            ss.setBooking(booking);
            seatStatusRepository.save(ss);
        }

        if (voucher != null) {
            voucherUsageRepository.save(VoucherUsage.builder()
                    .voucher(voucher)
                    .user(user)
                    .booking(booking)
                    .usedAt(LocalDateTime.now())
                    .build());
        }

        return sba301.fe.edu.vn.besba.dto.BookingResponse.builder()
                .bookingId(booking.getId())
                .totalAmount(booking.getTotalAmount())
                .status(booking.getStatus())
                .createdAt(booking.getCreatedAt())
                .tickets(Collections.emptyList())
                .build();
    }

    @Transactional
    public sba301.fe.edu.vn.besba.dto.BookingResponse confirmBooking(Integer bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new CustomException(404, "Không tìm thấy đơn hàng", HttpStatus.NOT_FOUND));

        if (!"PENDING".equals(booking.getStatus())) {
            throw new CustomException(400, "Trạng thái đơn hàng không hợp lệ để xác nhận", HttpStatus.BAD_REQUEST);
        }

        voucherUsageRepository.findByBookingId(bookingId).ifPresent(usage -> {
            Voucher voucher = usage.getVoucher();
            voucher.setUsedCount(voucher.getUsedCount() + 1);
            voucherRepository.save(voucher);
        });

        List<SeatStatus> seatStatuses = seatStatusRepository.findByBooking_Id(bookingId);
        List<Ticket> tickets = new ArrayList<>();

        for (SeatStatus ss : seatStatuses) {
            Ticket ticket = ticketRepository.save(Ticket.builder()
                    .booking(booking)
                    .seat(ss.getSeat())
                    .ticketPrice(calculatePrice(ss.getSeat().getSeatType(), booking.getShowtime().getBasePrice()))
                    .qrCode(UUID.randomUUID().toString())
                    .checkInStatus(false)
                    .build());
            tickets.add(ticket);

            ss.setStatus("BOOKED");
            ss.setUser(null);
            seatStatusRepository.save(ss);
        }

        booking.setStatus("PAID");
        bookingRepository.save(booking);

        return sba301.fe.edu.vn.besba.dto.BookingResponse.builder()
                .bookingId(booking.getId())
                .totalAmount(booking.getTotalAmount())
                .status(booking.getStatus())
                .createdAt(booking.getCreatedAt())
                .tickets(tickets.stream().map(t -> TicketResponse.builder()
                        .ticketId(t.getId())
                        .seatName(t.getSeat().getRowName() + t.getSeat().getSeatNumber())
                        .price(t.getTicketPrice())
                        .qrCode(t.getQrCode())
                        .build()).collect(Collectors.toList()))
                .build();
    }

    @Transactional
    public void cancelBooking(Integer bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new CustomException(404, "Không tìm thấy đơn hàng", HttpStatus.NOT_FOUND));

        if (!"PENDING".equals(booking.getStatus())) {
            throw new CustomException(400, "Chỉ có thể hủy đơn hàng đang chờ thanh toán", HttpStatus.BAD_REQUEST);
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

    @Transactional
    public void cancelBookingByAdmin(Integer bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new CustomException(404, "Không tìm thấy đơn hàng", HttpStatus.NOT_FOUND));

        if ("CANCELLED".equals(booking.getStatus())) {
            throw new CustomException(400, "Đơn hàng đã được hủy trước đó!", HttpStatus.BAD_REQUEST);
        }

        booking.setStatus("CANCELLED");
        bookingRepository.save(booking);

        List<Integer> seatIds = booking.getTickets().stream()
                .map(t -> t.getSeat().getId())
                .collect(Collectors.toList());

        if (!seatIds.isEmpty()) {
            List<SeatStatus> seatStatuses = seatStatusRepository.findByShowtimeIdAndSeatIdIn(booking.getShowtime().getId(), seatIds);
            for (SeatStatus ss : seatStatuses) {
                ss.setStatus("AVAILABLE");
                ss.setUser(null);
                ss.setBooking(null);
            }
            seatStatusRepository.saveAll(seatStatuses);
        }
    }

    private double calculatePrice(String seatType, Double basePrice) {
        return switch (seatType.toUpperCase()) {
            case "VIP" -> basePrice * 1.5;
            case "COUPLE" -> basePrice * 2.0;
            default -> basePrice;
        };
    }

    @Transactional
    public void checkInBooking(Integer bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new CustomException(404, "Không tìm thấy đơn vé!", HttpStatus.NOT_FOUND));

        if (!"PAID".equals(booking.getStatus()) && !"CONFIRMED".equals(booking.getStatus())) {
            throw new CustomException(400, "Chỉ có thể soát vé cho đơn hàng đã thanh toán thành công!", HttpStatus.BAD_REQUEST);
        }

        booking.setStatus("COMPLETED");
        bookingRepository.save(booking);

        List<Ticket> tickets = booking.getTickets();
        if (tickets != null && !tickets.isEmpty()) {
            for (Ticket ticket : tickets) {
                if (!ticket.getCheckInStatus()) {
                    ticket.setCheckInStatus(true);
                    ticket.setCheckInTime(LocalDateTime.now());
                }
            }
            ticketRepository.saveAll(tickets);
        }
    }

    public List<BookingResponse> getMyBookings(Integer userId) {
        return bookingRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(BookingResponse::fromEntity)
                .toList();
    }

    public sba301.fe.edu.vn.besba.dto.BookingResponse getBookingById(Integer bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new CustomException(404, "Booking not found", null));

        List<TicketResponse> ticketResponses = ticketRepository.findByBooking_Id(bookingId).stream()
                .map(t -> TicketResponse.builder()
                        .ticketId(t.getId())
                        .seatName(t.getSeat().getRowName() + t.getSeat().getSeatNumber())
                        .price(t.getTicketPrice())
                        .qrCode(t.getQrCode())
                        .build())
                .collect(Collectors.toList());

        return sba301.fe.edu.vn.besba.dto.BookingResponse.builder()
                .bookingId(booking.getId())
                .totalAmount(booking.getTotalAmount())
                .status(booking.getStatus())
                .createdAt(booking.getCreatedAt())
                .tickets(ticketResponses)
                .build();
    }

    public Boolean checkExistPendingBooking() {
        UserPrincipal currentUser = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new CustomException(404, "Không tìm thấy người dùng", HttpStatus.NOT_FOUND));

        boolean hasPending = bookingRepository.existsByUserIdAndStatus(user.getId(), "PENDING");
        return hasPending;
    }
}