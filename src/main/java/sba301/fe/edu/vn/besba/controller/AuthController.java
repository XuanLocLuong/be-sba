package sba301.fe.edu.vn.besba.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import sba301.fe.edu.vn.besba.base.BaseResponse;
import sba301.fe.edu.vn.besba.dto.request.AuthRequest;
import sba301.fe.edu.vn.besba.dto.request.ForgotPasswordRequest;
import sba301.fe.edu.vn.besba.dto.request.ResetPasswordRequest;
import sba301.fe.edu.vn.besba.dto.response.AuthResponse;
import sba301.fe.edu.vn.besba.dto.request.RegisterRequest;
import sba301.fe.edu.vn.besba.entity.User;
import sba301.fe.edu.vn.besba.security.JwtTokenUtil;
import sba301.fe.edu.vn.besba.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor

public class AuthController {
    private final AuthService authService;
    private final JwtTokenUtil jwtTokenUtil;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        User user = authService.authenticate(request.getUsername(), request.getPassword());
        String token = jwtTokenUtil.generateToken(user.getUsername());

        return ResponseEntity.ok(new AuthResponse(
                token,
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getEmail(),
                user.getRole().getRoleName()
        ));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        User user = authService.register(request);
        String token = jwtTokenUtil.generateToken(user.getUsername());

        return ResponseEntity.ok(new AuthResponse(
                token,
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getEmail(),
                user.getRole().getRoleName()
        ));
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<BaseResponse<String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request.getEmail());
        return ResponseEntity.ok(BaseResponse.success("Mã OTP đã được gửi đến email của bạn."));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<BaseResponse<String>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request.getEmail(), request.getOtp(), request.getNewPassword());
        return ResponseEntity.ok(BaseResponse.success("Đổi mật khẩu thành công. Bạn có thể đăng nhập lại."));
    }
    @PostMapping("/register-staff")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<AuthResponse> registerStaff(@Valid @RequestBody RegisterRequest request) {
        User user = authService.registerStaff(request);
        return ResponseEntity.ok(new AuthResponse(
                null,
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getEmail(),
                user.getRole().getRoleName()
        ));
    }
}
