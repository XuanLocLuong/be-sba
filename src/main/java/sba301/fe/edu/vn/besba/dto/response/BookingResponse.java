package sba301.fe.edu.vn.besba.dto.response;

import lombok.Builder;
import lombok.Data;
import sba301.fe.edu.vn.besba.entity.Booking;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class BookingResponse {
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

    public static BookingResponse fromEntity(Booking booking) {
        return BookingResponse.builder()
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
                .build();
    }
}