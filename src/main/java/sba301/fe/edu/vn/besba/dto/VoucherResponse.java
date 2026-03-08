package sba301.fe.edu.vn.besba.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
    private Date expiryDate;
}