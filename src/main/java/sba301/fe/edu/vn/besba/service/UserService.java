package sba301.fe.edu.vn.besba.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sba301.fe.edu.vn.besba.dto.request.ChangePasswordRequest;
import sba301.fe.edu.vn.besba.dto.request.UpdateProfileRequest;
import sba301.fe.edu.vn.besba.dto.response.UserProfileResponse;
import sba301.fe.edu.vn.besba.entity.User;
import sba301.fe.edu.vn.besba.exception.CustomException;
import sba301.fe.edu.vn.besba.repository.UserRepository;
import sba301.fe.edu.vn.besba.security.UserPrincipal;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserProfileResponse getMyProfile(UserPrincipal currentUser) {
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new CustomException(404, "Không tìm thấy người dùng", HttpStatus.NOT_FOUND));
        return UserProfileResponse.fromEntity(user);
    }

    @Transactional
    public UserProfileResponse updateProfile(UserPrincipal currentUser, UpdateProfileRequest request) {
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new CustomException(404, "Không tìm thấy người dùng", HttpStatus.NOT_FOUND));

        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());

        return UserProfileResponse.fromEntity(userRepository.save(user));
    }

    @Transactional
    public void changePassword(UserPrincipal currentUser, ChangePasswordRequest request) {
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new CustomException(404, "Không tìm thấy người dùng", HttpStatus.NOT_FOUND));

        if ("GOOGLE".equalsIgnoreCase(user.getProvider())) {
            throw new CustomException(400, "Tài khoản Google không thể đổi mật khẩu tại đây. Vui lòng đổi tại tài khoản Google của bạn.", HttpStatus.BAD_REQUEST);
        }

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new CustomException(400, "Mật khẩu cũ không chính xác", HttpStatus.BAD_REQUEST);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Transactional
    public void toggleUserStatus(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new CustomException(404, "User not found", HttpStatus.NOT_FOUND));

        if ("ACTIVE".equals(user.getStatus())) {
            user.setStatus("INACTIVE");
        } else {
            user.setStatus("ACTIVE");
        }
        userRepository.save(user);
    }

    public List<UserProfileResponse> getUsersByRole(String roleName) {
        return userRepository.findAll().stream()
                .filter(u -> u.getRole().getRoleName().equals(roleName))
                .map(UserProfileResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserProfileResponse adminUpdateUser(Integer id, UpdateProfileRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new CustomException(404, "Không tìm thấy người dùng", HttpStatus.NOT_FOUND));

        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());

        return UserProfileResponse.fromEntity(userRepository.save(user));
    }
}