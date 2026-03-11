package sba301.fe.edu.vn.besba.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sba301.fe.edu.vn.besba.dto.request.ShowtimeRequest;
import sba301.fe.edu.vn.besba.dto.response.ShowtimeResponse;
import sba301.fe.edu.vn.besba.entity.*;
import sba301.fe.edu.vn.besba.exception.CustomException;
import sba301.fe.edu.vn.besba.repository.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShowtimeService {

    private final ShowtimeRepository showtimeRepository;
    private final MovieRepository movieRepository;
    private final RoomRepository roomRepository;
    private final SeatRepository seatRepository;
    private final SeatStatusRepository seatStatusRepository;

    // Lấy tất cả lịch chiếu
    public List<ShowtimeResponse> getAllShowtimes() {
        return showtimeRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Lấy lịch chiếu theo phim
    public List<ShowtimeResponse> getCurrentShowtimeByMovie(Integer movieId) {
        List<Showtime> showtimes = showtimeRepository.findCurrentShowtimeByMovieId(movieId, LocalDateTime.now());
        return showtimes.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ShowtimeResponse createShowtime(ShowtimeRequest request) {
        Movie movie = movieRepository.findById(request.getMovieId())
                .orElseThrow(() -> new CustomException(404, "Phim không tồn tại", HttpStatus.NOT_FOUND));
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new CustomException(404, "Phòng không tồn tại", HttpStatus.NOT_FOUND));

        LocalDateTime calculatedEndTime = request.getStartTime().plusMinutes(movie.getDurationMinutes() + 15);

        // Kiểm tra trùng lịch phòng
        if (showtimeRepository.isRoomBusy(room.getId(), request.getStartTime(), calculatedEndTime, -1)) {
            throw new CustomException(400, "Phòng chiếu đã có lịch vào khoảng thời gian này!", HttpStatus.BAD_REQUEST);
        }

        Showtime showtime = showtimeRepository.save(Showtime.builder()
                .movie(movie).room(room).startTime(request.getStartTime())
                .endTime(calculatedEndTime).basePrice(request.getBasePrice())
                .status("SCHEDULED").build());

        List<Seat> seats = seatRepository.findByRoomIdOrderByRowNameAscSeatNumberAsc(room.getId());
        List<SeatStatus> seatStatuses = seats.stream().map(seat -> SeatStatus.builder()
                .showtime(showtime).seat(seat).status("AVAILABLE").build())
                .collect(Collectors.toList());
        seatStatusRepository.saveAll(seatStatuses);

        return convertToDto(showtime);
    }

    @Transactional
    public void cancelShowtime(Integer id) {
        Showtime showtime = showtimeRepository.findById(id)
                .orElseThrow(() -> new CustomException(404, "Không tìm thấy lịch chiếu", HttpStatus.NOT_FOUND));
        showtime.setStatus("CANCELLED");
        showtimeRepository.save(showtime);
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
                .status(showtime.getStatus())
                .build();
    }
}