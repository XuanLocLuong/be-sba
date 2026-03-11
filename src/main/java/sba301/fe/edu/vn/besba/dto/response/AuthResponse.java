package sba301.fe.edu.vn.besba.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private Integer userId;
    private String username;
    private String fullName;
    private String email;
    private String role;
}
