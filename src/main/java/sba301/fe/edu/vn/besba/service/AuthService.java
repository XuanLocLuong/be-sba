package sba301.fe.edu.vn.besba.service;

import lombok.RequiredArgsConstructor;
import sba301.fe.edu.vn.besba.dto.RegisterRequest;
import sba301.fe.edu.vn.besba.entity.Role;
import sba301.fe.edu.vn.besba.entity.User;
import sba301.fe.edu.vn.besba.exception.CustomException;
import sba301.fe.edu.vn.besba.repository.RoleRepository;
import sba301.fe.edu.vn.besba.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public User authenticate(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(401, "Invalid username or password", HttpStatus.UNAUTHORIZED));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new CustomException(401, "Invalid username or password", HttpStatus.UNAUTHORIZED);
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
}
