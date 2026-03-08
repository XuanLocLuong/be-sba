package sba301.fe.edu.vn.besba.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sba301.fe.edu.vn.besba.base.BaseController;
import sba301.fe.edu.vn.besba.base.BaseResponse;
import sba301.fe.edu.vn.besba.dto.ShowtimeResponse;
import sba301.fe.edu.vn.besba.service.ShowtimeService;

import java.util.List;

@RestController
@RequestMapping("/api/showtimes")
@RequiredArgsConstructor
public class ShowtimeController extends BaseController {

    private final ShowtimeService showtimeService;

    @GetMapping("/public/{movieId}")
    public BaseResponse<List<ShowtimeResponse>> getCurrentShowtimeByMovie(@PathVariable Integer movieId) {
        return wrapSuccess(showtimeService.getCurrentShowtimeByMovie(movieId));
    }


}
