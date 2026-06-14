package com.group7.sis.service.queryservices;

import org.springframework.stereotype.Service;

import com.group7.sis.entity.AdminEntity;
import com.group7.sis.repository.AdminRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AdminQueryService {
    private final AdminRepository adminRepository;

    public AdminEntity getAdminEntity(Long id){
        return adminRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Admin not found."));
    }

    public long getTotalAdmins(){
        return adminRepository.count();
    }
}
