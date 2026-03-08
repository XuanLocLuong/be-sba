package sba301.fe.edu.vn.besba.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import sba301.fe.edu.vn.besba.dto.MovieRequest;
import sba301.fe.edu.vn.besba.dto.MovieResponse;
import sba301.fe.edu.vn.besba.entity.Movie;
import sba301.fe.edu.vn.besba.exception.CustomException;
import sba301.fe.edu.vn.besba.repository.MovieRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;

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

    public MovieResponse createMovie(MovieRequest request) {
        Movie movie = Movie.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .durationMinutes(request.getDurationMinutes())
                .releaseDate(request.getReleaseDate())
                .posterUrl(request.getPosterUrl())
                // Mặc định khi mới tạo là UPCOMING nếu không truyền vào
                .status(request.getStatus() != null ? request.getStatus() : "UPCOMING")
                .build();

        Movie savedMovie = movieRepository.save(movie);
        return MovieResponse.fromEntity(savedMovie);
    }

    public MovieResponse updateMovie(Integer id, MovieRequest request) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new CustomException(404, "Movie not found", HttpStatus.NOT_FOUND));

        movie.setTitle(request.getTitle());
        movie.setDescription(request.getDescription());
        movie.setDurationMinutes(request.getDurationMinutes());
        movie.setReleaseDate(request.getReleaseDate());
        movie.setPosterUrl(request.getPosterUrl());

        // Cập nhật trạng thái (có thể đổi từ UPCOMING -> ONGOING -> ENDED)
        if (request.getStatus() != null) {
            movie.setStatus(request.getStatus());
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