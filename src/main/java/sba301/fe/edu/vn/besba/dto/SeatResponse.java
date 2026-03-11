package sba301.fe.edu.vn.besba.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeatResponse {
    private Integer id;
    private String rowName;
    private Integer seatNumber;
    private String seatType;      // NORMAL, VIP, COUPLE
    private String status;        // AVAILABLE, RESERVED, BOOKED
    private Double price;
}
