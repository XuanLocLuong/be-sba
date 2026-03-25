package sba301.fe.edu.vn.besba.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RoomRequest {
    @NotBlank(message = "Tên phòng không được để trống")
    private String name;

    @NotNull(message = "Tổng số ghế không được để trống")
    @Min(value = 1, message = "Số lượng ghế phải lớn hơn hoặc bằng 1")
    private Integer totalSeats;

    private Integer vipSeatsCount = 0;
    private Integer coupleSeatsCount = 0;
    private String layoutType;
}