package sba301.fe.edu.vn.besba.dto.response;

import lombok.Builder;
import lombok.Data;
import sba301.fe.edu.vn.besba.entity.Booking;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class BookingResponseStaff {
    private Integer id;
    private String customerName;
    private String email;
    private String movieTitle;
    private String roomName;
    private LocalDateTime showtimeStart;
    private Double totalAmount;
    private String status;
    private LocalDateTime createdAt;

    private List<String> seats;

    private List<TicketInfo> tickets;

    @Data
    @Builder
    public static class TicketInfo {
        private Integer id;
        private String seatName;
        private String qrCode;
        private Boolean checkInStatus;
        private LocalDateTime checkInTime;
        private Boolean isCancelled;
    }

    public static BookingResponseStaff fromEntity(Booking booking) {
        return BookingResponseStaff.builder()
                .id(booking.getId())
                .customerName(booking.getUser().getFullName())
                .email(booking.getUser().getEmail())
                .movieTitle(booking.getShowtime().getMovie().getTitle())
                .roomName(booking.getShowtime().getRoom().getName())
                .showtimeStart(booking.getShowtime().getStartTime())
                .totalAmount(booking.getTotalAmount())
                .status(booking.getStatus())
                .createdAt(booking.getCreatedAt())
                .seats(booking.getTickets().stream()
                        .map(t -> t.getSeat().getRowName() + t.getSeat().getSeatNumber())
                        .toList())
                .tickets(booking.getTickets().stream()
                        .map(t -> TicketInfo.builder()
                                .id(t.getId())
                                .seatName(t.getSeat().getRowName() + t.getSeat().getSeatNumber())
                                .qrCode(t.getQrCode())
                                .checkInStatus(t.getCheckInStatus())
                                .checkInTime(t.getCheckInTime())
                                .isCancelled(t.getIsCancelled() != null ? t.getIsCancelled() : false)
                                .build())
                        .toList())
                .build();
    }
}