package sba301.fe.edu.vn.besba.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sba301.fe.edu.vn.besba.base.BaseController;
import sba301.fe.edu.vn.besba.base.BaseResponse;
import sba301.fe.edu.vn.besba.dto.request.ShowtimeRequest;
import sba301.fe.edu.vn.besba.dto.response.ShowtimeResponse;
import sba301.fe.edu.vn.besba.service.ShowtimeService;

import java.util.List;

@RestController
@RequestMapping("/api/showtimes")
@RequiredArgsConstructor
public class ShowtimeController extends BaseController {

    private final ShowtimeService showtimeService;

    // --- PUBLIC API

    @GetMapping("/public/{movieId}")
    public BaseResponse<List<ShowtimeResponse>> getCurrentShowtimeByMovie(@PathVariable Integer movieId) {
        return wrapSuccess(showtimeService.getCurrentShowtimeByMovie(movieId));
    }

    // --- ADMIN API

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('STAFF')")
    public BaseResponse<List<ShowtimeResponse>> getAllShowtimes() {
        return wrapSuccess(showtimeService.getAllShowtimes());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public BaseResponse<ShowtimeResponse> createShowtime(@Valid @RequestBody ShowtimeRequest request) {
        return wrapSuccess(showtimeService.createShowtime(request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public BaseResponse<String> cancelShowtime(@PathVariable Integer id) {
        showtimeService.cancelShowtime(id);
        return wrapSuccess("Hủy lịch chiếu thành công!");
    }
}