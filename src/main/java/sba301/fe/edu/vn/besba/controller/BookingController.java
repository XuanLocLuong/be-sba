package sba301.fe.edu.vn.besba.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sba301.fe.edu.vn.besba.base.BaseController;
import sba301.fe.edu.vn.besba.base.BaseResponse;
import sba301.fe.edu.vn.besba.dto.response.BookingResponse;
import sba301.fe.edu.vn.besba.service.BookingService;
import java.util.List;

@RestController
@RequestMapping("/api/admin/bookings")
@RequiredArgsConstructor
public class BookingController extends BaseController {
    private final BookingService bookingService;

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('STAFF')")
    public BaseResponse<List<BookingResponse>> getAllBookings() {
        return wrapSuccess(bookingService.getAllBookings());
    }

    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('STAFF')")
    public BaseResponse<String> cancelBooking(@PathVariable Integer id) {
        bookingService.cancelBooking(id);
        return wrapSuccess("Hủy đơn thành công và đã giải phóng ghế!");
    }


}