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

    public List<VoucherResponse> getAllVouchers() {
        return voucherRepository.findAll().stream()
                .map(this::convertToDto)
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

        return convertToDto(voucherRepository.save(voucher));
    }

    @Transactional
    public VoucherResponse updateVoucher(Integer id, VoucherRequest request) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new CustomException(404, "Không tìm thấy Voucher", HttpStatus.NOT_FOUND));

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

        return convertToDto(voucherRepository.save(voucher));
    }

    @Transactional
    public void deleteVoucher(Integer id) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new CustomException(404, "Không tìm thấy Voucher", HttpStatus.NOT_FOUND));

        voucher.setStatus(voucher.getStatus() == 1 ? 0 : 1);
        voucherRepository.save(voucher);
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