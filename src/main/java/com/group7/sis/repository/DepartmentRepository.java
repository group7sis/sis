package com.group7.sis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.group7.sis.entity.academic.DepartmentEntity;
import java.util.List;

public interface DepartmentRepository extends JpaRepository<DepartmentEntity, Long>{
    @Query("""
        SELECT d FROM DepartmentEntity d
        WHERE (:name IS NULL OR LOWER(d.name) LIKE LOWER(CONCAT('%', :name, '%')))        
    """)
    List<DepartmentEntity> search(@Param("name") String name);   
}
