package sba301.fe.edu.vn.besba.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sba301.fe.edu.vn.besba.base.BaseController;
import sba301.fe.edu.vn.besba.base.BaseResponse;
import sba301.fe.edu.vn.besba.dto.response.DashboardResponse;
import sba301.fe.edu.vn.besba.service.DashboardService;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class DashboardController extends BaseController {
    private final DashboardService dashboardService;

    @GetMapping("/stats")
    @PreAuthorize("hasAuthority('ADMIN')")
    public BaseResponse<DashboardResponse> getStats() {
        return wrapSuccess(dashboardService.getDashboardStats());
    }
}