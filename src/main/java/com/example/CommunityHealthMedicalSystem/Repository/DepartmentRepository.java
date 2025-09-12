package com.example.CommunityHealthMedicalSystem.Repository;

import com.example.CommunityHealthMedicalSystem.Model.Department;
import com.example.CommunityHealthMedicalSystem.Model.MedicalStaff;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
    Optional<Department> findByNameIgnoreCase(String name);
    List<Department> findByMedicalStaffs(MedicalStaff medicalStaff);
    List<Department> findMedicalStaff(MedicalStaff medicalStaff);
}
