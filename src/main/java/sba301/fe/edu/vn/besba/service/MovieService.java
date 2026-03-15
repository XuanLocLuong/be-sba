package sba301.fe.edu.vn.besba.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import sba301.fe.edu.vn.besba.dto.request.MovieRequest;
import sba301.fe.edu.vn.besba.dto.response.MovieResponse;
import sba301.fe.edu.vn.besba.entity.Movie;
import sba301.fe.edu.vn.besba.exception.CustomException;
import sba301.fe.edu.vn.besba.repository.MovieRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;
    private final GoogleDriveService googleDriveService;

    public List<MovieResponse> getAllMovies() {
        return movieRepository.findAll().stream()
                .map(MovieResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<MovieResponse> getActiveMovies() {
        return movieRepository.findByStatus("ONGOING").stream()
                .map(MovieResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public MovieResponse getMovieById(Integer id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new CustomException(404, "Movie not found", HttpStatus.NOT_FOUND));
        return MovieResponse.fromEntity(movie);
    }

    @Transactional
    public MovieResponse createMovie(MovieRequest request, MultipartFile posterFile) {
        // Upload ảnh lên Google Drive
        String posterUrl = null;
        if (posterFile != null && !posterFile.isEmpty()) {
            try {
                posterUrl = googleDriveService.uploadImage(posterFile);
            } catch (Exception e) {
                throw new CustomException(500, "Lỗi upload ảnh: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            throw new CustomException(400, "Vui lòng chọn ảnh poster", HttpStatus.BAD_REQUEST);
        }

        Movie movie = Movie.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .durationMinutes(request.getDurationMinutes())
                .releaseDate(request.getReleaseDate())
                .posterUrl(posterUrl)
                .status(request.getStatus() != null ? request.getStatus() : "UPCOMING")
                .build();

        Movie savedMovie = movieRepository.save(movie);
        return MovieResponse.fromEntity(savedMovie);
    }

    @Transactional
    public MovieResponse updateMovie(Integer id, MovieRequest request, MultipartFile posterFile) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new CustomException(404, "Movie not found", HttpStatus.NOT_FOUND));

        // Cập nhật các trường text
        movie.setTitle(request.getTitle());
        movie.setDescription(request.getDescription());
        movie.setDurationMinutes(request.getDurationMinutes());
        movie.setReleaseDate(request.getReleaseDate());
        if (request.getStatus() != null) {
            movie.setStatus(request.getStatus());
        }

        // Xử lý ảnh mới nếu có
        if (posterFile != null && !posterFile.isEmpty()) {
            try {
                // Xóa ảnh cũ trên Drive (nếu có)
                String oldUrl = movie.getPosterUrl();
                if (oldUrl != null && oldUrl.contains("/d/")) {
                    String fileId = googleDriveService.extractIdFromUrl(oldUrl);
                    if (fileId != null) {
                        googleDriveService.deleteImage(fileId);
                    }
                }
                // Upload ảnh mới
                String newUrl = googleDriveService.uploadImage(posterFile);
                movie.setPosterUrl(newUrl);
            } catch (Exception e) {
                throw new CustomException(500, "Lỗi upload ảnh: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        Movie updatedMovie = movieRepository.save(movie);
        return MovieResponse.fromEntity(updatedMovie);
    }

    public void deleteMovie(Integer id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new CustomException(404, "Movie not found", HttpStatus.NOT_FOUND));

        // Soft Delete: Không xóa cứng để bảo toàn dữ liệu lịch sử đặt vé (Bookings) và suất chiếu (Showtimes)
        // Chuyển trạng thái sang INACTIVE
        movie.setStatus("INACTIVE");

        movieRepository.save(movie);
    }

    public List<MovieResponse> getUpcomingMovies() {
        return movieRepository.findByStatus("UPCOMING").stream()
                .map(MovieResponse::fromEntity)
                .collect(Collectors.toList());
    }
}