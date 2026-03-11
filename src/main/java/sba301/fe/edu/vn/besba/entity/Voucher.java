package sba301.fe.edu.vn.besba.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "Vouchers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Voucher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String code;

    @Column(name = "discount_percent")
    private Integer discountPercent;

    @Column(name = "max_discount_amount")
    private Double maxDiscountAmount;

    @Column(name = "min_order_value")
    private Double minOrderValue;

    @Builder.Default
    private Integer status = 1; // 1: Active, 0: Inactive

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "used_count")
    @Builder.Default
    private Integer usedCount = 0;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "expiry_date")
    private Date expiryDate;

    @PrePersist
    protected void onStart() {
        if (startDate == null) {
            startDate = LocalDateTime.now();
        }
    }

    @Column(name = "image_url")
    private String imageUrl;
}