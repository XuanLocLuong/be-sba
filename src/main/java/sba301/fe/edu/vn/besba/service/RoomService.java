package sba301.fe.edu.vn.besba.service;

import sba301.fe.edu.vn.besba.dto.request.RoomRequest;
import sba301.fe.edu.vn.besba.dto.response.RoomResponse;
import sba301.fe.edu.vn.besba.dto.response.SeatResponse;

import java.util.List;

public interface RoomService {
    List<RoomResponse> getAllRooms();
    RoomResponse getRoomById(Integer id);
    RoomResponse createRoom(RoomRequest request);
    RoomResponse updateRoom(Integer id, RoomRequest request);
    void deleteRoom(Integer id);
    List<SeatResponse> getSeatsByRoomId(Integer roomId);
}