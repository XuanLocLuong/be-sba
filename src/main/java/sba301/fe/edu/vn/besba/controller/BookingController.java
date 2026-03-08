package sba301.fe.edu.vn.besba.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sba301.fe.edu.vn.besba.base.BaseController;
import sba301.fe.edu.vn.besba.base.BaseResponse;
import sba301.fe.edu.vn.besba.dto.BookingRequest;
import sba301.fe.edu.vn.besba.dto.BookingResponse;
import sba301.fe.edu.vn.besba.service.BookingService;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController extends BaseController {

    private final BookingService bookingService;

    @PostMapping
    public BaseResponse<BookingResponse> createBooking(@RequestBody BookingRequest request) {
        return wrapSuccess(bookingService.createBooking(request));
    }

    @PostMapping("/{bookingId}/confirm")
    public BaseResponse<BookingResponse> confirmBooking(@PathVariable Integer bookingId) {
        return wrapSuccess(bookingService.confirmBooking(bookingId));
    }

    @PostMapping("/{bookingId}/cancel")
    public BaseResponse<Void> cancelBooking(@PathVariable Integer bookingId) {
        bookingService.cancelBooking(bookingId);
        return wrapSuccess(null);
    }
}
