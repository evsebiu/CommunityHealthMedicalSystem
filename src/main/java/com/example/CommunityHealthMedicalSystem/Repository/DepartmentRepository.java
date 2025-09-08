package com.example.CommunityHealthMedicalSystem.Repository;

import com.example.CommunityHealthMedicalSystem.Model.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
    Optional<Department> findByNameIgnoreCase(String name);

}
