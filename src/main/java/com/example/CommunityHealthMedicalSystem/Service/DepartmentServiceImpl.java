package com.example.CommunityHealthMedicalSystem.Service;

import com.example.CommunityHealthMedicalSystem.DTO.DepartmentDTO;
import com.example.CommunityHealthMedicalSystem.Exception.DuplicateResourceException;
import com.example.CommunityHealthMedicalSystem.Exception.ResourceNotFound;
import com.example.CommunityHealthMedicalSystem.Model.Department;
import com.example.CommunityHealthMedicalSystem.Model.MedicalStaff;
import com.example.CommunityHealthMedicalSystem.Repository.DepartmentRepository;
import org.springframework.stereotype.Service;
import com.example.CommunityHealthMedicalSystem.Exception.IllegalArgumentException;

import java.util.List;
import java.util.Optional;

@Service
public class DepartmentServiceImpl implements DepartmentService{

    private final DepartmentRepository departmentRepo;

    public DepartmentServiceImpl(DepartmentRepository departmentRepo){
        this.departmentRepo = departmentRepo;
    }

    @Override
    public List<Department> getAllDepartments(){
        return departmentRepo.findAll();
    }

    @Override
    public Optional<Department> getDepartmentById(Long id){
        return departmentRepo.findById(id);
    }

    @Override
    public Optional<Department> getDepartmentByNameIgnoreCase(String name){
        return departmentRepo.findByNameIgnoreCase(name);
    }

    @Override
    public List<Department> getDepartmentByMedicalStaff(MedicalStaff medicalStaff){
        if (medicalStaff == null){
            throw new IllegalArgumentException("Medical staff cannot be null.");
        }
        return departmentRepo.findByMedicalStaffs(medicalStaff);
    }
    @Override
    public DepartmentDTO createDepartment(DepartmentDTO departmentDTO){
        if (departmentDTO == null) {
            throw new IllegalArgumentException("Department cannot be null.");
        }

        Optional<Department> existingByName = departmentRepo.findByNameIgnoreCase(departmentDTO.getName());
        if (existingByName.isPresent()){
            throw new DuplicateResourceException("Department with name " + departmentDTO.getName() + " already exits.");
        }

        Department department = new Department();
        department.setDescription(departmentDTO.getDescription());
        department.setName(departmentDTO.getName());

        Department savedDepartment = departmentRepo.save(department);
        System.out.println("Department " + savedDepartment.getName() + " successfully created." );
        return convertToDTO(savedDepartment);
    }

    @Override
    public DepartmentDTO updateDepartment(Long id, DepartmentDTO departmentDTO){
        // 1.find existing appointment.
        Department department = departmentRepo.findById(id)
                .orElseThrow(()-> new ResourceNotFound("Department with id:" +
                        id + " not found."));

        // 2. validate input.
        if (departmentDTO.getName() != null){
            department.setName(departmentDTO.getName());
        }
        if (departmentDTO.getDescription()!=null){
            department.setDescription(departmentDTO.getDescription());
        }
        // 3. return and update.
        Department updateDepartment = departmentRepo.save(department);
        return convertToDTO(updateDepartment);
    }

    @Override
    public void deleteDepartment(Long id){
        Department department = departmentRepo.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("Department not found " + id));

        departmentRepo.delete(department);
    }

    @Override
    public List<MedicalStaff> getMedicalStaffByDepartment(Long departmentId){
        Optional<Department> department= departmentRepo.findById(departmentId);
        if (departmentId == null){
            throw new IllegalArgumentException("ID cannot be null.");
        }
        departmentRepo.findById(departmentId)
                .orElseThrow(()-> new ResourceNotFound("Department not found " +
                        "with id: " + departmentId));

        return departmentRepo.findMedicalStaffByDepartment(departmentId);
    }

    public DepartmentDTO convertToDTO(Department department){
        DepartmentDTO dto = new DepartmentDTO();

        dto.setId(department.getId());
        dto.setName(department.getName());
        dto.setDescription(department.getDescription());

        dto.setMedicalStaffCount(
            department.getMedicalStaffs() != null ? department.getMedicalStaffs().size() : 0);

        dto.setActiveAppointmentsCount(
                department.getAppointments() != null ? department.getAppointments().size() : 0);

        return dto;
    }
}
