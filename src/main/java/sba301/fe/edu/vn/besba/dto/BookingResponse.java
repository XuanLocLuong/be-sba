package sba301.fe.edu.vn.besba.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingResponse {
    private Integer bookingId;
    private Double totalAmount;
    private String status;
    private LocalDateTime createdAt;
    private List<TicketResponse> tickets;
}
