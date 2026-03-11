package sba301.fe.edu.vn.besba.dto.response;

import lombok.Builder;
import lombok.Data;
import sba301.fe.edu.vn.besba.entity.Showtime;
import java.time.LocalDateTime;

@Data
@Builder
public class ShowtimeResponse {
    private Integer id;
    private Integer movieId;
    private String movieTitle;
    private String posterUrl;
    private Integer roomId;
    private String roomName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Double basePrice;
    private String status;
    private Integer availableSeats;

    public static ShowtimeResponse fromEntity(Showtime showtime) {
        return ShowtimeResponse.builder()
                .id(showtime.getId())
                .movieId(showtime.getMovie().getId())
                .movieTitle(showtime.getMovie().getTitle())
                .posterUrl(showtime.getMovie().getPosterUrl())
                .roomId(showtime.getRoom().getId())
                .roomName(showtime.getRoom().getName())
                .startTime(showtime.getStartTime())
                .endTime(showtime.getEndTime())
                .basePrice(showtime.getBasePrice())
                .status(showtime.getStatus())
                .build();
    }
}