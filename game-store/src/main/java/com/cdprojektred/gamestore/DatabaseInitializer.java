package com.cdprojektred.gamestore;

import com.cdprojektred.gamestore.model.Role;
import com.cdprojektred.gamestore.model.UserEntity;
import com.cdprojektred.gamestore.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class DatabaseInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        userRepository.save(
                new UserEntity(
                        null,
                        "admin",
                        passwordEncoder.encode("admin"),
                        Role.ADMIN.name()
                )
        );
    }
}