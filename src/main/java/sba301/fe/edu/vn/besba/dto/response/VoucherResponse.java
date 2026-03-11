package sba301.fe.edu.vn.besba.dto.response;

import lombok.Builder;
import lombok.Data;
import sba301.fe.edu.vn.besba.entity.Voucher;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@Builder
public class VoucherResponse {
    private Integer id;
    private String code;
    private Integer discountPercent;
    private Double maxDiscountAmount;
    private Double minOrderValue;
    private Integer status;
    private Integer quantity;
    private Integer usedCount;
    private LocalDateTime startDate;
    private String imageUrl;
    private Date expiryDate;

    public static VoucherResponse fromEntity(Voucher voucher) {
        return VoucherResponse.builder()
                .id(voucher.getId())
                .code(voucher.getCode())
                .discountPercent(voucher.getDiscountPercent())
                .maxDiscountAmount(voucher.getMaxDiscountAmount())
                .minOrderValue(voucher.getMinOrderValue())
                .status(voucher.getStatus())
                .quantity(voucher.getQuantity())
                .usedCount(voucher.getUsedCount())
                .startDate(voucher.getStartDate())
                .expiryDate(voucher.getExpiryDate())
                .imageUrl(voucher.getImageUrl())
                .build();
    }
}