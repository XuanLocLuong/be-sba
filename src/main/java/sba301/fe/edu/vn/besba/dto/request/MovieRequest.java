package sba301.fe.edu.vn.besba.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class MovieRequest {
    @NotBlank(message = "Tiêu đề phim không được để trống")
    private String title;

    private String description;

    @NotNull(message = "Thời lượng phim là bắt buộc")
    @Min(value = 1, message = "Thời lượng phải lớn hơn 0 phút")
    private Integer durationMinutes;

    @NotNull(message = "Ngày phát hành là bắt buộc")
    private LocalDate releaseDate;

    private String posterUrl;

    private String status; // UPCOMING, ONGOING, ENDED
}