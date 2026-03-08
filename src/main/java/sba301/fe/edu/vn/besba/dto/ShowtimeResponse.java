package sba301.fe.edu.vn.besba.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShowtimeResponse {
    private Integer id;
    private Integer movieId;
    private String movieTitle;
    private Integer roomId;
    private String roomName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Double basePrice;
    private Integer availableSeats;
}
