package sba301.fe.edu.vn.besba.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import sba301.fe.edu.vn.besba.dto.request.RoomRequest;
import sba301.fe.edu.vn.besba.dto.response.RoomResponse;
import sba301.fe.edu.vn.besba.dto.response.SeatResponse;
import sba301.fe.edu.vn.besba.entity.Room;
import sba301.fe.edu.vn.besba.entity.Seat;
import sba301.fe.edu.vn.besba.exception.CustomException;
import sba301.fe.edu.vn.besba.repository.RoomRepository;
import sba301.fe.edu.vn.besba.repository.SeatRepository;
import sba301.fe.edu.vn.besba.service.RoomService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final SeatRepository seatRepository;

    @Override
    public List<RoomResponse> getAllRooms() {
        return roomRepository.findAll().stream()
                .map(RoomResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public RoomResponse getRoomById(Integer id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new CustomException(404, "Không tìm thấy phòng chiếu", HttpStatus.NOT_FOUND));
        return RoomResponse.fromEntity(room);
    }

    @Override
    public RoomResponse createRoom(RoomRequest request) {
        if (roomRepository.existsByName(request.getName())) {
            throw new CustomException(400, "Tên phòng chiếu đã tồn tại", HttpStatus.BAD_REQUEST);
        }

        Room room = Room.builder()
                .name(request.getName())
                .totalSeats(request.getTotalSeats())
                .build();

        Room savedRoom = roomRepository.save(room);

        // Tự động sinh ghế
        int seatsPerRow = 10;
        int rows = (int) Math.ceil((double) request.getTotalSeats() / seatsPerRow);
        for (int i = 0; i < rows; i++) {
            char rowName = (char) ('A' + i); // Hàng A, B, C...
            for (int j = 1; j <= seatsPerRow; j++) {
                if ((i * seatsPerRow + j) > request.getTotalSeats()) break;
                Seat seat = Seat.builder()
                        .room(savedRoom)
                        .rowName(String.valueOf(rowName))
                        .seatNumber(j)
                        .seatType("NORMAL")
                        .build();
                seatRepository.save(seat);
            }
        }
        if (request.getTotalSeats() < 1) {
            throw new CustomException(400, "Số lượng ghế phải lớn hơn hoặc bằng 1", HttpStatus.BAD_REQUEST);
        }
        return RoomResponse.fromEntity(savedRoom);
    }

    @Override
    public List<SeatResponse> getSeatsByRoomId(Integer roomId) {
        return seatRepository.findByRoomIdOrderByRowNameAscSeatNumberAsc(roomId)
                .stream()
                .map(SeatResponse::fromEntity)
                .toList();
    }

    @Override
    public RoomResponse updateRoom(Integer id, RoomRequest request) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new CustomException(404, "Không tìm thấy phòng chiếu", HttpStatus.NOT_FOUND));

        if (!room.getName().equals(request.getName()) && roomRepository.existsByName(request.getName())) {
            throw new CustomException(400, "Tên phòng chiếu đã tồn tại", HttpStatus.BAD_REQUEST);
        }

        room.setName(request.getName());
        room.setTotalSeats(request.getTotalSeats());

        return RoomResponse.fromEntity(roomRepository.save(room));
    }

    @Override
    public void deleteRoom(Integer id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new CustomException(404, "Không tìm thấy phòng chiếu", HttpStatus.NOT_FOUND));
        roomRepository.delete(room);
    }
}