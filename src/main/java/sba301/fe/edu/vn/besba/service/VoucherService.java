package sba301.fe.edu.vn.besba.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sba301.fe.edu.vn.besba.dto.VoucherResponse;
import sba301.fe.edu.vn.besba.entity.Voucher;
import sba301.fe.edu.vn.besba.repository.VoucherRepository;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VoucherService {

    private final VoucherRepository voucherRepository;

    public List<VoucherResponse> getActiveVoucher() {
        Date now = new Date();
        List<Voucher> vouchers = voucherRepository.findActiveVouchers(1, now);

        return vouchers.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private VoucherResponse convertToDto(Voucher voucher) {
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
                .build();
    }
}
