package com.example.CommunityHealthMedicalSystem.Service;

import com.example.CommunityHealthMedicalSystem.Model.Department;
import com.example.CommunityHealthMedicalSystem.Model.MedicalStaff;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface DepartmentService {
    Optional<Department> getDepartmentById(Long id);
    Optional<Department> getDepartmentByNameIgnoreCase(String name);
    List<Department> getDepartmentByMedicalStaff(MedicalStaff medicalStaff);
    List<Department> getAllDepartments(Department department);

    Department createDepartment(Long id, Department department, MedicalStaff medicalStaff);
    Department updateDepartment(Long id, Department departmentDetails);
    void deleteDepartment(Long id);

    boolean isDepartmentAvailable(Long departmentId);
    List<MedicalStaff> getMedicalStaffByDepartment(Long departmentId);
}
