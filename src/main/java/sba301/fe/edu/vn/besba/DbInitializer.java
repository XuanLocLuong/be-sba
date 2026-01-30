package sba301.fe.edu.vn.besba;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import sba301.fe.edu.vn.besba.entity.Role;
import sba301.fe.edu.vn.besba.entity.User;
import sba301.fe.edu.vn.besba.repository.RoleRepository;
import sba301.fe.edu.vn.besba.repository.UserRepository;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DbInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        createRoles();

        createAdmin();

        createStaffs();
    }

    private void createRoles() {
        if (roleRepository.count() == 0) {
            Role adminRole = Role.builder()
                    .roleName("ADMIN")
                    .build();
            roleRepository.save(adminRole);

            Role staffRole = Role.builder()
                    .roleName("STAFF")
                    .build();
            roleRepository.save(staffRole);

            Role userRole = Role.builder()
                    .roleName("USER")
                    .build();
            roleRepository.save(userRole);

            System.out.println("✅ Created roles: ADMIN, STAFF, USER");
        }
    }

    private void createAdmin() {
        if (!userRepository.existsByUsername("admin")) {
            Role adminRole = roleRepository.findByRoleName("ADMIN")
                    .orElseThrow(() -> new RuntimeException("Admin role not found"));

            User admin = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .fullName("System Administrator")
                    .email("admin@cinema.com")
                    .phone("0123456789")
                    .role(adminRole)
                    .createdAt(LocalDateTime.now())
                    .build();

            userRepository.save(admin);
            System.out.println("Created admin: admin / admin123");
        }
    }

    private void createStaffs() {
        createStaff("staff1", "Staff Member 1", "staff1@cinema.com", "0987654321");
        createStaff("staff2", "Staff Member 2", "staff2@cinema.com", "0987654322");
    }

    private void createStaff(String username, String fullName, String email, String phone) {
        if (!userRepository.existsByUsername(username)) {
            Role staffRole = roleRepository.findByRoleName("STAFF")
                    .orElseThrow(() -> new RuntimeException("Staff role not found"));

            User staff = User.builder()
                    .username(username)
                    .password(passwordEncoder.encode("staff123"))
                    .fullName(fullName)
                    .email(email)
                    .phone(phone)
                    .role(staffRole)
                    .createdAt(LocalDateTime.now())
                    .build();

            userRepository.save(staff);
            System.out.println("Created " + username + ": " + username + " / staff123");
        }
    }
}
