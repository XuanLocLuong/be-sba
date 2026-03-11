package sba301.fe.edu.vn.besba.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class VoucherRequest {
    @NotBlank(message = "Mã voucher không được để trống")
    private String code;

    @NotNull(message = "Phần trăm giảm giá không được để trống")
    @Min(value = 1, message = "Giảm giá tối thiểu 1%")
    @Max(value = 100, message = "Giảm giá tối đa 100%")
    private Integer discountPercent;

    private Double maxDiscountAmount;
    private Double minOrderValue;

    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 1, message = "Số lượng tối thiểu là 1")
    private Integer quantity;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    private LocalDateTime startDate;

    @NotNull(message = "Ngày kết thúc không được để trống")
    private Date expiryDate;

    private String imageUrl;
}