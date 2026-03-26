package sba301.fe.edu.vn.besba.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sba301.fe.edu.vn.besba.base.BaseController;
import sba301.fe.edu.vn.besba.base.BaseResponse;
import sba301.fe.edu.vn.besba.dto.ReserveSeatRequest;
import sba301.fe.edu.vn.besba.dto.ShowtimeSeatsResponse;
import sba301.fe.edu.vn.besba.service.SeatService;

@RestController
@RequestMapping("/api/seats")
@RequiredArgsConstructor
public class SeatController extends BaseController {

    private final SeatService seatService;

    @GetMapping("/public/showtime/{showtimeId}")
    public BaseResponse<ShowtimeSeatsResponse> getSeatsByShowtime(@PathVariable Integer showtimeId) {
        return wrapSuccess(seatService.getSeatsByShowtime(showtimeId));
    }

    @PostMapping("/reserve")
    public BaseResponse<Void> reserveSeats(@RequestBody ReserveSeatRequest request) {
        seatService.reserveSeats(request.getShowtimeId(), request.getSeatIds());
        return wrapSuccess(null);
    }

    @PostMapping("/release")
    public BaseResponse<Void> releaseSeats(@RequestParam Integer showtimeId) {
        seatService.releaseReservedSeats(showtimeId);
        return wrapSuccess(null);
    }

    @PostMapping("/toggle-hold")
    public BaseResponse<Void> toggleSeatHold(@RequestBody sba301.fe.edu.vn.besba.dto.ToggleSeatRequest request) {
        seatService.toggleSeatHold(request.getShowtimeId(), request.getSeatId());
        return wrapSuccess(null);
    }

}
