package com.gestion.planning.config;

import com.gestion.planning.model.Role;
import com.gestion.planning.model.User;
import com.gestion.planning.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Créer un admin par défaut s'il n'existe pas
        if (!userRepository.existsByUsername("admin")) {
            User admin = User.builder()
                    .username("admin")
                    .email("admin@planning.com")
                    .password(passwordEncoder.encode("admin123"))
                    .firstName("Admin")
                    .lastName("System")
                    .department("Administration")
                    .role(Role.ADMIN)
                    .build();

            userRepository.save(admin);
            log.info("==============================================");
            log.info("Admin par défaut créé avec succès !");
            log.info("Username: admin");
            log.info("Password: admin123");
            log.info("==============================================");
        }
    }
}
