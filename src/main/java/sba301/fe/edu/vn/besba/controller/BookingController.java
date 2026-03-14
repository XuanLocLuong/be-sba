package sba301.fe.edu.vn.besba.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import sba301.fe.edu.vn.besba.base.BaseController;
import sba301.fe.edu.vn.besba.base.BaseResponse;
import sba301.fe.edu.vn.besba.dto.BookingRequest;
import sba301.fe.edu.vn.besba.security.UserPrincipal;
import sba301.fe.edu.vn.besba.service.BookingService;
import sba301.fe.edu.vn.besba.dto.response.BookingResponseStaff;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BookingController extends BaseController {

    private final BookingService bookingService;

    // --- ENDPOINTS CHO USER 

    @PostMapping("/bookings")
    public BaseResponse<sba301.fe.edu.vn.besba.dto.BookingResponse> createBooking(@RequestBody BookingRequest request) {
        return wrapSuccess(bookingService.createBooking(request));
    }

    @PostMapping("/bookings/{bookingId}/confirm")
    public BaseResponse<sba301.fe.edu.vn.besba.dto.BookingResponse> confirmBooking(@PathVariable Integer bookingId) {
        return wrapSuccess(bookingService.confirmBooking(bookingId));
    }

    @PostMapping("/bookings/{bookingId}/cancel")
    public BaseResponse<Void> cancelBookingByUser(@PathVariable Integer bookingId) {
        bookingService.cancelBooking(bookingId);
        return wrapSuccess(null);
    }

    @GetMapping("/bookings/my-bookings")
    public BaseResponse<List<sba301.fe.edu.vn.besba.dto.response.BookingResponse>> getMyBookings(
            @AuthenticationPrincipal UserPrincipal currentUser) {
        Integer userId = currentUser.getId();
        return wrapSuccess(bookingService.getMyBookings(userId));
    }

    @GetMapping("/bookings/{bookingId}")
    public BaseResponse<sba301.fe.edu.vn.besba.dto.BookingResponse> getBookingById(@PathVariable Integer bookingId) {
        return wrapSuccess(bookingService.getBookingById(bookingId));
    }

    @GetMapping("/user/checkBookings")
    public BaseResponse<Boolean> checkExistPendingBooking(){
        return wrapSuccess(bookingService.checkExistPendingBooking());
    }

    // --- ENDPOINTS CHO ADMIN/STAFF

    @GetMapping("/admin/bookings")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('STAFF')")
    public BaseResponse<List<BookingResponseStaff>> getAllBookings() {
        return wrapSuccess(bookingService.getAllBookings());
    }

    @PutMapping("/admin/bookings/{id}/cancel")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('STAFF')")
    public BaseResponse<String> cancelBookingByAdmin(@PathVariable Integer id) {
        bookingService.cancelBooking(id);
        return wrapSuccess("Hủy đơn thành công và đã giải phóng ghế!");
    }
    @PutMapping("/admin/bookings/{id}/check-in")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('STAFF')")
    public BaseResponse<String> checkInBookingByAdmin(@PathVariable Integer id) {
        bookingService.checkInBooking(id);
        return wrapSuccess("Soát vé toàn bộ đơn thành công!");
    }
}