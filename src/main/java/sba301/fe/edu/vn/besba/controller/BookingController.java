package sba301.fe.edu.vn.besba.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sba301.fe.edu.vn.besba.base.BaseController;
import sba301.fe.edu.vn.besba.base.BaseResponse;
import sba301.fe.edu.vn.besba.dto.BookingRequest;
import sba301.fe.edu.vn.besba.dto.response.BookingResponse;
import sba301.fe.edu.vn.besba.service.BookingService;

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

    @GetMapping("/bookings/{bookingId}")
    public BaseResponse<sba301.fe.edu.vn.besba.dto.BookingResponse> getBookingById(@PathVariable Integer bookingId) {
        return wrapSuccess(bookingService.getBookingById(bookingId));
    }
    // --- ENDPOINTS CHO ADMIN/STAFF

    @GetMapping("/admin/bookings")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('STAFF')")
    public BaseResponse<List<BookingResponse>> getAllBookings() {
        return wrapSuccess(bookingService.getAllBookings());
    }

    @PutMapping("/admin/bookings/{id}/cancel")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('STAFF')")
    public BaseResponse<String> cancelBookingByAdmin(@PathVariable Integer id) {
        bookingService.cancelBooking(id);
        return wrapSuccess("Hủy đơn thành công và đã giải phóng ghế!");
    }
}