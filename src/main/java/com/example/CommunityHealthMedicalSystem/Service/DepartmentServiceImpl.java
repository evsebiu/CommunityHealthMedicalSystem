package com.example.CommunityHealthMedicalSystem.Service;

import com.example.CommunityHealthMedicalSystem.Exception.DuplicateResourceException;
import com.example.CommunityHealthMedicalSystem.Exception.ResourceNotFound;
import com.example.CommunityHealthMedicalSystem.Model.Department;
import com.example.CommunityHealthMedicalSystem.Model.MedicalStaff;
import com.example.CommunityHealthMedicalSystem.Model.Patient;
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
    public List<Department> getAllDepartments(Department department){
        return departmentRepo.findAll();
    }

    @Override
    public Department createDepartment(Long id, Department department, MedicalStaff medicalStaff){
        if (department == null){
            throw new IllegalArgumentException("Department cannot be null.");
        }
        if (medicalStaff == null){
            throw new java.lang.IllegalArgumentException("Medical staff cannot be null.");
        }


        Optional<Department> existingByName = departmentRepo.findByNameIgnoreCase(department.getName());
        if (existingByName.isPresent()){
            throw new DuplicateResourceException("Department with name " + department.getName() + " already exits.");
        }

        Department savedDepartment = departmentRepo.save(department);
        System.out.println("Department " + savedDepartment.getName() + " successfully created." );
        return savedDepartment;
    }

    @Override
    public Department updateDepartment(Long id, Department departmentDetails){

        Department department = departmentRepo.findById(id)
                .orElseThrow(()-> new ResourceNotFound("Department with id " + id + " does not exist."));

        if (departmentDetails.getName() != null){
            department.setName(departmentDetails.getName());
        }
        if (departmentDetails.getName() != null){
            department.setDescription(departmentDetails.getDescription());
        }
        return departmentRepo.save(department);
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
            throw new java.lang.IllegalArgumentException("ID cannot be null.");
        }
        return department.get().getMedicalStaffs();
    }
}
