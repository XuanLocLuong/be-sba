package sba301.fe.edu.vn.besba.dto.response;

import lombok.Builder;
import lombok.Data;
import sba301.fe.edu.vn.besba.entity.Seat;

@Data
@Builder
public class SeatResponse {
    private Integer id;
    private String rowName;
    private Integer seatNumber;
    private String seatType;
    private Double additionalPrice;

    public static SeatResponse fromEntity(Seat seat) {
        return SeatResponse.builder()
                .id(seat.getId())
                .rowName(seat.getRowName())
                .seatNumber(seat.getSeatNumber())
                .seatType(seat.getSeatType())
                .additionalPrice(seat.getAdditionalPrice())
                .build();
    }
}