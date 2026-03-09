package sba301.fe.edu.vn.besba.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sba301.fe.edu.vn.besba.dto.request.VoucherRequest;
import sba301.fe.edu.vn.besba.dto.response.VoucherResponse;
import sba301.fe.edu.vn.besba.entity.Voucher;
import sba301.fe.edu.vn.besba.exception.CustomException;
import sba301.fe.edu.vn.besba.repository.VoucherRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VoucherService {

    private final VoucherRepository voucherRepository;

    public List<VoucherResponse> getAllVouchers() {
        return voucherRepository.findAll().stream()
                .map(VoucherResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public VoucherResponse createVoucher(VoucherRequest request) {
        if (voucherRepository.existsByCode(request.getCode())) {
            throw new CustomException(400, "Mã Voucher đã tồn tại", HttpStatus.BAD_REQUEST);
        }

        Voucher voucher = Voucher.builder()
                .code(request.getCode())
                .discountPercent(request.getDiscountPercent())
                .maxDiscountAmount(request.getMaxDiscountAmount())
                .minOrderValue(request.getMinOrderValue())
                .quantity(request.getQuantity())
                .startDate(request.getStartDate())
                .expiryDate(request.getExpiryDate())
                .status(1)
                .usedCount(0)
                .build();

        return VoucherResponse.fromEntity(voucherRepository.save(voucher));
    }

    @Transactional
    public VoucherResponse updateVoucher(Integer id, VoucherRequest request) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new CustomException(404, "Không tìm thấy Voucher", HttpStatus.NOT_FOUND));

        // Kiểm tra trùng mã code nếu có đổi code mới
        if (!voucher.getCode().equals(request.getCode()) && voucherRepository.existsByCode(request.getCode())) {
            throw new CustomException(400, "Mã Voucher đã tồn tại", HttpStatus.BAD_REQUEST);
        }

        voucher.setCode(request.getCode());
        voucher.setDiscountPercent(request.getDiscountPercent());
        voucher.setMaxDiscountAmount(request.getMaxDiscountAmount());
        voucher.setMinOrderValue(request.getMinOrderValue());
        voucher.setQuantity(request.getQuantity());
        voucher.setStartDate(request.getStartDate());
        voucher.setExpiryDate(request.getExpiryDate());

        return VoucherResponse.fromEntity(voucherRepository.save(voucher));
    }

    @Transactional
    public void deleteVoucher(Integer id) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new CustomException(404, "Không tìm thấy Voucher", HttpStatus.NOT_FOUND));

        if (voucher.getStatus() == 1) {
            voucher.setStatus(0);
        } else {
            voucher.setStatus(1);
        }
        voucherRepository.save(voucher);
    }
}