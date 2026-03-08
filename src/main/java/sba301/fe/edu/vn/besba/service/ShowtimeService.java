package sba301.fe.edu.vn.besba.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sba301.fe.edu.vn.besba.dto.ShowtimeResponse;
import sba301.fe.edu.vn.besba.entity.Showtime;
import sba301.fe.edu.vn.besba.repository.ShowtimeRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShowtimeService {

    private final ShowtimeRepository showtimeRepository;

    public List<ShowtimeResponse> getCurrentShowtimeByMovie(Integer movieId) {
        List<Showtime> showtimes = showtimeRepository.findCurrentShowtimeByMovieId(movieId, LocalDateTime.now());
        return showtimes.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private ShowtimeResponse convertToDto(Showtime showtime) {
        int availableSeats = showtimeRepository.countAvailableSeatsByShowtimeId(showtime.getId());
        return ShowtimeResponse.builder()
                .id(showtime.getId())
                .movieId(showtime.getMovie().getId())
                .movieTitle(showtime.getMovie().getTitle())
                .roomId(showtime.getRoom().getId())
                .roomName(showtime.getRoom().getName())
                .startTime(showtime.getStartTime())
                .endTime(showtime.getEndTime())
                .basePrice(showtime.getBasePrice())
                .availableSeats(availableSeats)
                .build();
    }
}
