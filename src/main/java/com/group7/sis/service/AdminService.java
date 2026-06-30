package com.group7.sis.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.group7.sis.dto.AdminDTO;
import com.group7.sis.entity.AdminEntity;
import com.group7.sis.mapper.AdminMapper;
import com.group7.sis.repository.AdminRepository;
import com.group7.sis.service.queryservices.AdminQueryService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AdminService {
    private final AdminRepository adminRepository;
    private final AdminMapper adminMapper;
    private final AdminQueryService adminQueryService;

    public AdminEntity addAdmin(AdminDTO adminDTO){
        AdminEntity adminEntity = adminMapper.toEntity(adminDTO);
        return adminRepository.save(adminEntity);
    }

    public AdminDTO updateAdmin (Long id, AdminDTO adminDTO){
        AdminEntity admin = adminQueryService.getAdminEntity(id);
        adminMapper.updateEntityFromDto(adminDTO, admin);
        adminRepository.save(admin);
        return getAdmin(id);
    }

    public AdminDTO getAdmin(Long id){
        return adminMapper.toDto(adminQueryService.getAdminEntity(id));
    }

    public List<AdminDTO> getAllAdmin(){
        List<AdminDTO> admins = new ArrayList<>();

        for (AdminEntity admin : adminRepository.findAll()){
            admins.add(adminMapper.toDto(admin));
        }
        return admins;
    }
}
