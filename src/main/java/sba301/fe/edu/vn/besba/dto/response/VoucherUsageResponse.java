package sba301.fe.edu.vn.besba.dto.response;

import lombok.Builder;
import lombok.Data;
import sba301.fe.edu.vn.besba.entity.VoucherUsage;
import java.time.LocalDateTime;

@Data
@Builder
public class VoucherUsageResponse {
    private Integer id;
    private String voucherCode;
    private String customerName;
    private Integer bookingId;
    private LocalDateTime usedAt;
    private Double discountAmount;

    public static VoucherUsageResponse fromEntity(VoucherUsage usage) {
        return VoucherUsageResponse.builder()
                .id(usage.getId())
                .voucherCode(usage.getVoucher().getCode())
                .customerName(usage.getUser().getFullName())
                .bookingId(usage.getBooking().getId())
                .usedAt(usage.getUsedAt())
                .build();
    }
}