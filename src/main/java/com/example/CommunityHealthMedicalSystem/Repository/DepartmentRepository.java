package com.example.CommunityHealthMedicalSystem.Repository;

import com.example.CommunityHealthMedicalSystem.Model.Department;
import com.example.CommunityHealthMedicalSystem.Model.MedicalStaff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
    List<Department> findByNameIgnoreCase(String name);
    List<Department> findByMedicalStaffs(Long medicalStaffId);

    @Query("SELECT ms FROM Department d Join d.medicalStaffs ms WHERE d.id= :departmentId")
    List<MedicalStaff> findMedicalStaffByDepartment(@Param("departmentId") Long departmentId);
}
