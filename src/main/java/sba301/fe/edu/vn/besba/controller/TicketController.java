package sba301.fe.edu.vn.besba.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sba301.fe.edu.vn.besba.base.BaseController;
import sba301.fe.edu.vn.besba.base.BaseResponse;
import sba301.fe.edu.vn.besba.service.TicketService;

@RestController
@RequestMapping("/api/admin/tickets")
@RequiredArgsConstructor
public class TicketController extends BaseController {
    private final TicketService ticketService;

    @PatchMapping("/check-in/{qrCode}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('STAFF')")
    public BaseResponse<String> checkIn(@PathVariable String qrCode) {
        return wrapSuccess(ticketService.checkInTicket(qrCode));
    }

    @PutMapping("/cancel/{qrCode}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('STAFF')")
    public BaseResponse<String> cancelSingleTicket(@PathVariable String qrCode) {
        ticketService.cancelSingleTicket(qrCode);
        return wrapSuccess("Hủy vé và giải phóng ghế thành công!");
    }
}