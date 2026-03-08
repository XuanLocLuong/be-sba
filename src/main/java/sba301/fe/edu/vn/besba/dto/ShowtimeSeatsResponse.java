package sba301.fe.edu.vn.besba.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShowtimeSeatsResponse {
    private Integer showtimeId;
    private String movieTitle;
    private String roomName;
    private List<SeatResponse> seats;
}
