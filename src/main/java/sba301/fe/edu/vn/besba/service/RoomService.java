package sba301.fe.edu.vn.besba.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sba301.fe.edu.vn.besba.dto.request.RoomRequest;
import sba301.fe.edu.vn.besba.dto.response.RoomResponse;
import sba301.fe.edu.vn.besba.dto.response.SeatResponse;
import sba301.fe.edu.vn.besba.entity.Room;
import sba301.fe.edu.vn.besba.entity.Seat;
import sba301.fe.edu.vn.besba.exception.CustomException;
import sba301.fe.edu.vn.besba.repository.RoomRepository;
import sba301.fe.edu.vn.besba.repository.SeatRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final SeatRepository seatRepository;

    public List<RoomResponse> getAllRooms() {
        return roomRepository.findAll().stream()
                .map(RoomResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public RoomResponse getRoomById(Integer id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new CustomException(404, "Không tìm thấy phòng chiếu", HttpStatus.NOT_FOUND));
        return RoomResponse.fromEntity(room);
    }

    @Transactional
    public RoomResponse createRoom(RoomRequest request) {
        if (roomRepository.existsByName(request.getName())) {
            throw new CustomException(400, "Tên phòng chiếu đã tồn tại", HttpStatus.BAD_REQUEST);
        }

        Room room = Room.builder()
                .name(request.getName())
                .totalSeats(request.getTotalSeats())
                .build();
        Room savedRoom = roomRepository.save(room);

        int total = request.getTotalSeats();
        int vipCount = request.getVipSeatsCount() != null ? request.getVipSeatsCount() : 0;
        int coupleCount = request.getCoupleSeatsCount() != null ? request.getCoupleSeatsCount() : 0;
        int normalCount = total - vipCount - coupleCount;

        if (normalCount < 0) {
            throw new CustomException(400, "Tổng số ghế VIP và Couple vượt quá tổng số ghế của phòng", HttpStatus.BAD_REQUEST);
        }

        String layout = request.getLayoutType() != null ? request.getLayoutType() : "DEFAULT";
        String[] seatTypes = new String[total];
        for (int i = 0; i < total; i++) seatTypes[i] = "NORMAL";

        if (layout.equals("VIP_FRONT")) {
            for (int i = 0; i < vipCount; i++) seatTypes[i] = "VIP";
            for (int i = total - coupleCount; i < total; i++) seatTypes[i] = "COUPLE";
        } else if (layout.equals("COUPLE_FRONT")) {
            for (int i = 0; i < coupleCount; i++) seatTypes[i] = "COUPLE";
            for (int i = total - vipCount; i < total; i++) seatTypes[i] = "VIP";
        } else if (layout.equals("VIP_SIDES")) {
            for (int i = total - coupleCount; i < total; i++) seatTypes[i] = "COUPLE";
            int vipsPlaced = 0;
            for (int distance = 0; distance < 5 && vipsPlaced < vipCount; distance++) {
                int leftCol = distance + 1;
                int rightCol = 10 - distance;
                for (int i = 0; i < total - coupleCount && vipsPlaced < vipCount; i++) {
                    int col = (i % 10) + 1;
                    if (col == leftCol || col == rightCol) {
                        seatTypes[i] = "VIP";
                        vipsPlaced++;
                    }
                }
            }
        } else { // DEFAULT
            for (int i = normalCount; i < total - coupleCount; i++) seatTypes[i] = "VIP";
            for (int i = total - coupleCount; i < total; i++) seatTypes[i] = "COUPLE";
        }

        int seatsPerRow = 10;
        for (int i = 0; i < total; i++) {
            int rowIdx = i / seatsPerRow;
            int colIdx = (i % seatsPerRow) + 1;
            char rowChar = (char) ('A' + rowIdx);

            String type = seatTypes[i];
            Double additionalPrice = 0.0;

            if (type.equals("COUPLE")) {
                additionalPrice = 50000.0;
            } else if (type.equals("VIP")) {
                additionalPrice = 20000.0;
            }

            Seat seat = Seat.builder()
                    .room(savedRoom)
                    .rowName(String.valueOf(rowChar))
                    .seatNumber(colIdx)
                    .seatType(type)
                    .additionalPrice(additionalPrice)
                    .build();
            seatRepository.save(seat);
        }

        return RoomResponse.fromEntity(savedRoom);
    }

    @Transactional
    public void updateSeatType(Integer seatId, String newType) {
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new CustomException(404, "Không tìm thấy ghế", HttpStatus.NOT_FOUND));

        seat.setSeatType(newType);
        switch (newType.toUpperCase()) {
            case "VIP" -> seat.setAdditionalPrice(20000.0);
            case "COUPLE" -> seat.setAdditionalPrice(50000.0);
            default -> seat.setAdditionalPrice(0.0);
        }
        seatRepository.save(seat);
    }

    public List<SeatResponse> getSeatsByRoomId(Integer roomId) {
        return seatRepository.findByRoomIdOrderByRowNameAscSeatNumberAsc(roomId)
                .stream()
                .map(SeatResponse::fromEntity)
                .toList();
    }

    @Transactional
    public RoomResponse updateRoom(Integer id, RoomRequest request) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new CustomException(404, "Không tìm thấy phòng chiếu", HttpStatus.NOT_FOUND));
        if (request.getName() != null && !room.getName().equals(request.getName())) {
            if (roomRepository.existsByName(request.getName())) {
                throw new CustomException(400, "Tên phòng chiếu đã tồn tại", HttpStatus.BAD_REQUEST);
            }
            room.setName(request.getName());
        }
        if (request.getTotalSeats() != null) {
            room.setTotalSeats(request.getTotalSeats());
        }

        return RoomResponse.fromEntity(roomRepository.save(room));
    }

    @Transactional
    public void deleteRoom(Integer id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new CustomException(404, "Không tìm thấy phòng chiếu", HttpStatus.NOT_FOUND));

        if ("ACTIVE".equals(room.getStatus())) {
            room.setStatus("INACTIVE");
        } else {
            room.setStatus("ACTIVE");
        }

        roomRepository.save(room);
    }
}