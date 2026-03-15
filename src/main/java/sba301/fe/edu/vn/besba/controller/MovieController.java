package sba301.fe.edu.vn.besba.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sba301.fe.edu.vn.besba.base.BaseController;
import sba301.fe.edu.vn.besba.base.BaseResponse;
import sba301.fe.edu.vn.besba.dto.request.MovieRequest;
import sba301.fe.edu.vn.besba.dto.response.MovieResponse;
import sba301.fe.edu.vn.besba.service.MovieService;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieController extends BaseController {

    private final MovieService movieService;

    // --- PUBLIC API (Không cần đăng nhập / Ai cũng xem được) ---
    
    // Lấy danh sách phim đang chiếu public
    @GetMapping("/public")
    public BaseResponse<List<MovieResponse>> getActiveMovies() {
        return wrapSuccess(movieService.getActiveMovies());
    }

    // Lấy danh sách phim đang chiếu public
    @GetMapping("/public/upcoming")
    public BaseResponse<List<MovieResponse>> getUpcomingMovies() {
        return wrapSuccess(movieService.getUpcomingMovies());
    }

    // Xem chi tiết một bộ phim
    @GetMapping("/public/{id}")
    public BaseResponse<MovieResponse> getMovieById(@PathVariable Integer id) {
        return wrapSuccess(movieService.getMovieById(id));
    }

    // ADMIN API

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public BaseResponse<List<MovieResponse>> getAllMovies() {
        return wrapSuccess(movieService.getAllMovies());
    }


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('ADMIN')")
    public BaseResponse<MovieResponse> createMovie(
            @RequestPart("movie") @Valid MovieRequest movieRequest,
            @RequestPart(value = "posterFile", required = false) MultipartFile posterFile) {
        return wrapSuccess(movieService.createMovie(movieRequest, posterFile));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('ADMIN')")
    public BaseResponse<MovieResponse> updateMovie(
            @PathVariable Integer id,
            @RequestPart("movie") @Valid MovieRequest movieRequest,
            @RequestPart(value = "posterFile", required = false) MultipartFile posterFile) {
        return wrapSuccess(movieService.updateMovie(id, movieRequest, posterFile));
    }

    // Xóa phim (Soft Delete -> Chuyển status sang INACTIVE)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public BaseResponse<String> deleteMovie(@PathVariable Integer id) {
        movieService.deleteMovie(id);
        return wrapSuccess("Deleted successfully (Status changed to INACTIVE)");
    }
}