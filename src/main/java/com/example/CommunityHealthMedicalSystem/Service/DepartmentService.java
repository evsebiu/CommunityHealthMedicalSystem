package com.example.CommunityHealthMedicalSystem.Service;

import com.example.CommunityHealthMedicalSystem.DTO.DepartmentDTO;
import com.example.CommunityHealthMedicalSystem.Model.Department;
import com.example.CommunityHealthMedicalSystem.Model.MedicalStaff;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface DepartmentService {
    Optional<Department> getDepartmentById(Long id);
    Optional<Department> getDepartmentByNameIgnoreCase(String name);
    List<Department> getDepartmentByMedicalStaff(Long medicalStaffId);
    List<Department> getAllDepartments();

    DepartmentDTO createDepartment(DepartmentDTO departmentDTO);
    DepartmentDTO updateDepartment(Long id, DepartmentDTO departmentDTO);
    void deleteDepartment(Long id);

    List<MedicalStaff> getMedicalStaffByDepartment(Long departmentId);
}
