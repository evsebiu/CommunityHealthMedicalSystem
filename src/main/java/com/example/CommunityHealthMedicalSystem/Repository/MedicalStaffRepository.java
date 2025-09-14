package com.example.CommunityHealthMedicalSystem.Repository;

import com.example.CommunityHealthMedicalSystem.Model.MedicalStaff;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MedicalStaffRepository extends JpaRepository<MedicalStaff, Long> {
    List<MedicalStaff> findByFirstNameContainsIgnoreCase(String firstName);
    List<MedicalStaff> findByLastNameContainsIgnoreCase(String lastName);
    Optional<MedicalStaff> findByEmailContainsIgnoreCase(String email);
    Optional<MedicalStaff> findByLicenseNumber(String licenseNumber);
    List<MedicalStaff> findBySpecializationContainsIgnoreCase(String specialization);
    List<MedicalStaff> findByRoleContains(MedicalStaff.Role role);
    List<MedicalStaff> findByDepartmentId(Long departmentId);
 }
