package sba301.fe.edu.vn.besba.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sba301.fe.edu.vn.besba.dto.SeatResponse;
import sba301.fe.edu.vn.besba.dto.ShowtimeSeatsResponse;
import sba301.fe.edu.vn.besba.entity.Seat;
import sba301.fe.edu.vn.besba.entity.SeatStatus;
import sba301.fe.edu.vn.besba.entity.Showtime;
import sba301.fe.edu.vn.besba.entity.User;
import sba301.fe.edu.vn.besba.exception.CustomException;
import sba301.fe.edu.vn.besba.repository.SeatStatusRepository;
import sba301.fe.edu.vn.besba.repository.ShowtimeRepository;
import sba301.fe.edu.vn.besba.repository.UserRepository;
import sba301.fe.edu.vn.besba.security.UserPrincipal;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeatService {

    private final ShowtimeRepository showtimeRepository;
    private final SeatStatusRepository seatStatusRepository;
    private final UserRepository userRepository;

    public ShowtimeSeatsResponse getSeatsByShowtime(Integer showtimeId){
        Showtime showtime = showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new CustomException(404, "Showtime not found", HttpStatus.NOT_FOUND));

        List<SeatStatus> seatStatuses = seatStatusRepository.findByShowtime_Id(showtimeId);

        List<SeatResponse> seatResponses = seatStatuses.stream()
                .map(seatStatus -> {
                    Seat seat = seatStatus.getSeat();
                    Double price = calculatePrice(seat.getSeatType(), showtime.getBasePrice());
                    return SeatResponse.builder()
                            .id(seat.getId())
                            .rowName(seat.getRowName())
                            .seatNumber(seat.getSeatNumber())
                            .seatType(seat.getSeatType())
                            .status(seatStatus.getStatus())
                            .price(price)
                            .build();
                })
                .collect(Collectors.toList());

        return ShowtimeSeatsResponse.builder()
                .showtimeId(showtimeId)
                .movieTitle(showtime.getMovie().getTitle())
                .roomName(showtime.getRoom().getName())
                .seats(seatResponses)
                .build();
    }

    private Double calculatePrice(String seatType, Double basePrice) {
        return switch (seatType) {
            case "VIP" -> basePrice * 1.5;
            case "COUPLE" -> basePrice * 2.0;
            default -> basePrice;
        };
    }

    @Transactional
    public void reserveSeats(Integer showtimeId, List<Integer> seatIds) {
        UserPrincipal currentUser = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new CustomException(404, "User not found", HttpStatus.NOT_FOUND));

        for (Integer seatId : seatIds) {
            SeatStatus seatStatus = seatStatusRepository.findByShowtime_IdAndSeat_Id(showtimeId, seatId)
                    .orElseThrow(() -> new CustomException(404, "Seat not found in this showtime", HttpStatus.NOT_FOUND));


            if (!"AVAILABLE".equals(seatStatus.getStatus())) {
                throw new CustomException(400, "Seat " + seatId + " is not available", HttpStatus.BAD_REQUEST);
            }

            seatStatus.setStatus("RESERVED");
            seatStatus.setUser(user);
            seatStatusRepository.save(seatStatus);
        }
    }

    @Transactional
    public void releaseReservedSeats(Integer showtimeId) {
        UserPrincipal currentUser = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        releaseReservedSeats(showtimeId, currentUser.getId());
    }

    @Transactional
    public void releaseReservedSeats(Integer showtimeId, Integer userId) {
        List<SeatStatus> reservedSeats = seatStatusRepository
                .findByShowtime_Id(showtimeId)
                .stream()
                .filter(seatStatus -> seatStatus.getStatus().equals("RESERVED")
                        && seatStatus.getUser() != null
                        && seatStatus.getUser().getId().equals(userId))
                .collect(Collectors.toList());

        for (SeatStatus ss : reservedSeats) {
            ss.setStatus("AVAILABLE");
            ss.setUser(null);
        }
    }

    @Transactional
    public void toggleSeatHold(Integer showtimeId, Integer seatId) {
        UserPrincipal currentUser = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new CustomException(404, "User not found", HttpStatus.NOT_FOUND));

        SeatStatus seatStatus = seatStatusRepository.findByShowtime_IdAndSeat_Id(showtimeId, seatId)
                .orElseThrow(() -> new CustomException(404, "Seat not found in this showtime", HttpStatus.NOT_FOUND));

        if ("AVAILABLE".equals(seatStatus.getStatus())) {
            seatStatus.setStatus("RESERVED");
            seatStatus.setUser(user);
        } else if ("RESERVED".equals(seatStatus.getStatus()) && seatStatus.getUser() != null && seatStatus.getUser().getId().equals(user.getId())) {
            seatStatus.setStatus("AVAILABLE");
            seatStatus.setUser(null);
        } else {
            throw new CustomException(400, "Ghế này không khả dụng hoặc đã bị người khác giữ", HttpStatus.BAD_REQUEST);
        }

        seatStatusRepository.save(seatStatus);
    }

}
