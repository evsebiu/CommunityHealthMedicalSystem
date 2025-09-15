package com.example.CommunityHealthMedicalSystem.Service;


import com.example.CommunityHealthMedicalSystem.Model.Department;
import com.example.CommunityHealthMedicalSystem.Model.MedicalStaff;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface MedicalStaffService {
List<MedicalStaff> getByFirstNameIgnoreCase(String firstName);
List<MedicalStaff> getByLastNameIgnoreCase(String lastName);
Optional<MedicalStaff> getByEmailIgnoreCase(String email);
Optional<MedicalStaff> getByLicenseNumber(String licenseNumber);
List<MedicalStaff> getBySpecializationIgnoreCase(String specialization);
List<MedicalStaff> getByRoleIgnoreCase(MedicalStaff.Role role);
List<MedicalStaff> getByDepartment(Long departmentId);
Optional<MedicalStaff> getMedicalStaffById(Long id);

MedicalStaff createStaff(MedicalStaff medicalStaff);
void deleteStaff(Long id, MedicalStaff deletingStaff);
MedicalStaff updateStaff(Long id, MedicalStaff medicalStaffDetails, Department department);
}
