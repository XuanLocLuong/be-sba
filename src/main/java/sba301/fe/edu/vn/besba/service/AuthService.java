package sba301.fe.edu.vn.besba.service;

import lombok.RequiredArgsConstructor;
import sba301.fe.edu.vn.besba.dto.request.RegisterRequest;
import sba301.fe.edu.vn.besba.entity.Role;
import sba301.fe.edu.vn.besba.entity.User;
import sba301.fe.edu.vn.besba.exception.CustomException;
import sba301.fe.edu.vn.besba.repository.RoleRepository;
import sba301.fe.edu.vn.besba.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sba301.fe.edu.vn.besba.utils.EmailUtil;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailUtil emailUtil;
    public User authenticate(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(401, "Invalid username or password", HttpStatus.UNAUTHORIZED));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new CustomException(401, "Invalid username or password", HttpStatus.UNAUTHORIZED);
        }
        if ("INACTIVE".equals(user.getStatus())) {
            throw new CustomException(403, "Your account has been banned!", HttpStatus.FORBIDDEN);
        }
        return user;
    }

    @Transactional
    public User register(RegisterRequest request) {
        if (userRepository.existsByUsername((request.getUsername()))) {
            throw new CustomException(400, "Username already exists", HttpStatus.BAD_REQUEST);
        }

        if (userRepository.existsByEmail((request.getEmail()))){
            throw new CustomException(400, "Email already exists", HttpStatus.BAD_REQUEST);
        }

        Role userRole = roleRepository.findByRoleName("USER")
                .orElseThrow(() -> new CustomException(500, "Role not found", HttpStatus.INTERNAL_SERVER_ERROR));

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setRole(userRole);
        user.setCreatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    @Transactional
    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(404, "Không tìm thấy tài khoản với email này", HttpStatus.NOT_FOUND));

        String otp = String.format("%06d", new Random().nextInt(999999));
        user.setResetToken(otp);
        userRepository.save(user);

        emailUtil.sendOTP(user.getEmail(), otp, user.getFullName());
    }

    @Transactional
    public void resetPassword(String email, String otp, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(404, "Không tìm thấy tài khoản với email này", HttpStatus.NOT_FOUND));
        if (user.getResetToken() == null || !user.getResetToken().equals(otp)) {
            throw new CustomException(400, "Mã OTP không hợp lệ hoặc đã hết hạn", HttpStatus.BAD_REQUEST);
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        userRepository.save(user);
    }

    @Transactional
    public User registerStaff(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new CustomException(400, "Tên đăng nhập đã tồn tại", HttpStatus.BAD_REQUEST);
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(400, "Email đã tồn tại", HttpStatus.BAD_REQUEST);
        }

        Role staffRole = roleRepository.findByRoleName("STAFF")
                .orElseThrow(() -> new CustomException(500, "Role STAFF không tồn tại", HttpStatus.INTERNAL_SERVER_ERROR));

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .role(staffRole)
                .status("ACTIVE")
                .createdAt(LocalDateTime.now())
                .build();

        return userRepository.save(user);
    }
}
