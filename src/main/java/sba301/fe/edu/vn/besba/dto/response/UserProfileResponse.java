package sba301.fe.edu.vn.besba.dto.response;

import lombok.Builder;
import lombok.Data;
import sba301.fe.edu.vn.besba.entity.User;
import java.time.LocalDateTime;

@Data
@Builder
public class UserProfileResponse {
    private Integer id;
    private String username;
    private String fullName;
    private String email;
    private String phone;
    private String provider;
    private String status;
    private LocalDateTime createdAt;

    public static UserProfileResponse fromEntity(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .provider(user.getProvider())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .build();
    }
}