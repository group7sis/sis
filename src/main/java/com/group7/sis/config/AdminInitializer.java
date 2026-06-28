package com.group7.sis.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.group7.sis.entity.AdminEntity;
import com.group7.sis.repository.AdminRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements ApplicationRunner {

    private final AdminRepository adminRepository;

    @Override
    public void run(ApplicationArguments args) {
        if (adminRepository.count() == 0) {
            AdminEntity admin = new AdminEntity();
            admin.setUsername("Admin");
            admin.setPassword("password");
            adminRepository.save(admin);
        }
    }
}
