package sba301.fe.edu.vn.besba.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sba301.fe.edu.vn.besba.base.BaseController;
import sba301.fe.edu.vn.besba.base.BaseResponse;
import sba301.fe.edu.vn.besba.dto.request.VoucherRequest;
import sba301.fe.edu.vn.besba.dto.response.VoucherResponse;
import sba301.fe.edu.vn.besba.dto.response.VoucherUsageResponse;
import sba301.fe.edu.vn.besba.service.VoucherService;
import sba301.fe.edu.vn.besba.service.VoucherUsageService;

import java.util.List;

@RestController
@RequestMapping("/api/vouchers")
@RequiredArgsConstructor
public class VoucherController extends BaseController {

    private final VoucherService voucherService;
    private final VoucherUsageService voucherUsageService;

    // --- PUBLIC API
    
    @GetMapping("/public")
    public BaseResponse<List<VoucherResponse>> getActiveVoucher(){
        return wrapSuccess(voucherService.getActiveVoucher());
    }

    // --- ADMIN API

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public BaseResponse<List<VoucherResponse>> getAllVouchers() {
        return wrapSuccess(voucherService.getAllVouchers());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('ADMIN')")
    public BaseResponse<VoucherResponse> createVoucher(
            @RequestPart("voucher") @Valid VoucherRequest request,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile) {
        return wrapSuccess(voucherService.createVoucher(request, imageFile));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('ADMIN')")
    public BaseResponse<VoucherResponse> updateVoucher(
            @PathVariable Integer id,
            @RequestPart("voucher") @Valid VoucherRequest request,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile) {
        return wrapSuccess(voucherService.updateVoucher(id, request, imageFile));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public BaseResponse<String> deleteVoucher(@PathVariable Integer id) {
        voucherService.deleteVoucher(id);
        return wrapSuccess("Cập nhật trạng thái voucher thành công!");
    }

    @GetMapping("/{id}/usages")
    @PreAuthorize("hasAuthority('ADMIN')")
    public BaseResponse<List<VoucherUsageResponse>> getVoucherUsages(@PathVariable Integer id) {
        return wrapSuccess(voucherUsageService.getUsagesByVoucherId(id));
    }
}