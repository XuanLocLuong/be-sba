package sba301.fe.edu.vn.besba.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sba301.fe.edu.vn.besba.base.BaseController;
import sba301.fe.edu.vn.besba.base.BaseResponse;
import sba301.fe.edu.vn.besba.dto.VoucherResponse;
import sba301.fe.edu.vn.besba.service.VoucherService;

import java.util.List;

@RestController
@RequestMapping("/api/vouchers")
@RequiredArgsConstructor
public class VoucherController extends BaseController {

    private final VoucherService voucherService;

    @GetMapping("/public")
    public BaseResponse<List<VoucherResponse>> getActiveVoucher(){
        return wrapSuccess(voucherService.getActiveVoucher());
    }
}
