package sba301.fe.edu.vn.besba.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sba301.fe.edu.vn.besba.base.BaseController;
import sba301.fe.edu.vn.besba.base.BaseResponse;
import sba301.fe.edu.vn.besba.dto.request.RoomRequest;
import sba301.fe.edu.vn.besba.dto.response.RoomResponse;
import sba301.fe.edu.vn.besba.dto.response.SeatResponse;
import sba301.fe.edu.vn.besba.service.RoomService;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController extends BaseController {

    private final RoomService roomService;

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public BaseResponse<List<RoomResponse>> getAllRooms() {
        return wrapSuccess(roomService.getAllRooms());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public BaseResponse<RoomResponse> createRoom(@Valid @RequestBody RoomRequest request) {
        return wrapSuccess(roomService.createRoom(request));
    }

    @GetMapping("/{id}/seats")
    @PreAuthorize("hasAuthority('ADMIN')")
    public BaseResponse<List<SeatResponse>> getSeatsByRoomId(@PathVariable Integer id) {
        return wrapSuccess(roomService.getSeatsByRoomId(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public BaseResponse<RoomResponse> updateRoom(@PathVariable Integer id, @Valid @RequestBody RoomRequest request) {
        return wrapSuccess(roomService.updateRoom(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public BaseResponse<String> deleteRoom(@PathVariable Integer id) {
        roomService.deleteRoom(id);
        return wrapSuccess("Xóa phòng chiếu thành công!");
    }
}