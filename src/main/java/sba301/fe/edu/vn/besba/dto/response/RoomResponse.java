package sba301.fe.edu.vn.besba.dto.response;

import lombok.Builder;
import lombok.Data;
import sba301.fe.edu.vn.besba.entity.Room;

@Data
@Builder
public class RoomResponse {
    private Integer id;
    private String name;
    private Integer totalSeats;
    private String status;

    public static RoomResponse fromEntity(Room room) {
        return RoomResponse.builder()
                .id(room.getId())
                .name(room.getName())
                .totalSeats(room.getTotalSeats())
                .status(room.getStatus())
                .build();
    }
}