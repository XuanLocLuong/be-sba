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

    public List<ShowtimeResponse> getAllShowtimes() {
        return showtimeRepository.findAll().stream()
                .map(ShowtimeResponse::fromEntity)
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

        Showtime showtime = Showtime.builder()
                .movie(movie)
                .room(room)
                .startTime(request.getStartTime())
                .endTime(calculatedEndTime)
                .basePrice(request.getBasePrice())
                .status("SCHEDULED")
                .build();

        Showtime savedShowtime = showtimeRepository.save(showtime);

        List<Seat> seats = seatRepository.findByRoomIdOrderByRowNameAscSeatNumberAsc(room.getId());
        List<SeatStatus> seatStatuses = seats.stream().map(seat -> SeatStatus.builder()
                .showtime(savedShowtime)
                .seat(seat)
                .status("AVAILABLE")
                .build()).collect(Collectors.toList());
        seatStatusRepository.saveAll(seatStatuses);

        return ShowtimeResponse.fromEntity(savedShowtime);
    }

    @Transactional
    public void cancelShowtime(Integer id) {
        Showtime showtime = showtimeRepository.findById(id)
                .orElseThrow(() -> new CustomException(404, "Không tìm thấy lịch chiếu", HttpStatus.NOT_FOUND));

        showtime.setStatus("CANCELLED");
        showtimeRepository.save(showtime);
    }
}