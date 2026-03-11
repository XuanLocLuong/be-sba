package sba301.fe.edu.vn.besba.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ShowtimeRequest {
    @NotNull(message = "Vui lòng chọn phim")
    private Integer movieId;

    @NotNull(message = "Vui lòng chọn phòng chiếu")
    private Integer roomId;

    @NotNull(message = "Thời gian bắt đầu không được để trống")
    private LocalDateTime startTime;

    @NotNull(message = "Giá vé cơ bản không được để trống")
    @Min(value = 0, message = "Giá vé không hợp lệ")
    private Double basePrice;

    private String status;
}