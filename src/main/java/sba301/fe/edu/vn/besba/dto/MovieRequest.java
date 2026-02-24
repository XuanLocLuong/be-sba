package sba301.fe.edu.vn.besba.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class MovieRequest {
    @NotBlank(message = "Title cannot be empty")
    private String title;

    private String description;

    @NotNull(message = "Duration is required")
    private Integer durationMinutes;

    private LocalDate releaseDate;

    private String posterUrl;

    private String status; // UPCOMING, ONGOING, ENDED
}