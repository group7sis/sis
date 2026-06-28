
package com.group7.sis.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.group7.sis.entity.AdminEntity;

public interface AdminRepository extends JpaRepository<AdminEntity, Long>{
    Optional<AdminEntity> findByUsernameAndPassword(String username, String password);
}
