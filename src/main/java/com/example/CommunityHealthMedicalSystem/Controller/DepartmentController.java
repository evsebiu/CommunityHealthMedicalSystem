package com.example.CommunityHealthMedicalSystem.Controller;

import com.example.CommunityHealthMedicalSystem.DTO.DepartmentDTO;
import com.example.CommunityHealthMedicalSystem.Exception.ConflictException;
import com.example.CommunityHealthMedicalSystem.Exception.DuplicateResourceException;
import com.example.CommunityHealthMedicalSystem.Exception.IllegalArgumentException;
import com.example.CommunityHealthMedicalSystem.Exception.ResourceNotFound;
import com.example.CommunityHealthMedicalSystem.Model.Department;
import com.example.CommunityHealthMedicalSystem.Repository.DepartmentRepository;
import com.example.CommunityHealthMedicalSystem.Service.DepartmentServiceImpl;
import com.example.CommunityHealthMedicalSystem.Service.MedicalStaffServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/departments")
@Validated
public class DepartmentController {

    private final DepartmentServiceImpl departmentService;
    private final MedicalStaffServiceImpl medicalStaffService;
    private final DepartmentRepository departmentRepo;

    public DepartmentController(DepartmentServiceImpl departmentService, MedicalStaffServiceImpl medicalStaffService,
    DepartmentRepository departmentRepo){
        this.departmentService = departmentService;
        this.medicalStaffService = medicalStaffService;
        this.departmentRepo=departmentRepo;
    }

    @GetMapping
    public List<Department> getAllDepartments(){
        return departmentService.getAllDepartments();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Department> getDepartmentById(@PathVariable Long id){
        try{
            Optional<Department> department = departmentService.getDepartmentById(id);
            return department.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Department> getDepartmentByNameIgnoreCase(@PathVariable String name){
        Optional<Department> department = departmentService.getDepartmentByNameIgnoreCase(name);

        return department.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/get-by-staff/{medicalStaffId}")
    public ResponseEntity<List<Department>> getDepartmentByMedicalStaff(@PathVariable Long medicalStaffId) {

        List<Department> department = departmentService.getDepartmentByMedicalStaff(medicalStaffId);

        if (department.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(department);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Department>> searchDepartmentByName(@RequestParam(required = false) String name){
        try {
            List<Department> departments;

            if (name !=null && !name.trim().isEmpty()){
                departments = departmentRepo.findByNameIgnoreCase(name);
            } else {
                departments = departmentService.getAllDepartments();
            }

            if (departments.isEmpty()){
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(departments);
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    @PostMapping
    public ResponseEntity<DepartmentDTO> createDepartment(@RequestBody DepartmentDTO departmentDTO){

        try{
            DepartmentDTO savedDepartment = departmentService.createDepartment(departmentDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedDepartment);
        } catch (DuplicateResourceException e){
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/{id}/name")
    public ResponseEntity<DepartmentDTO> updateDepartmentName(@PathVariable Long id, @RequestParam String name) {

        try {

            // Create DTO and set name
            DepartmentDTO updateDepartmentName = new DepartmentDTO();
            updateDepartmentName.setName(name);

            // save updated
            DepartmentDTO updatedDepartment = departmentService.updateDepartmentName(id, updateDepartmentName);
            return ResponseEntity.ok(updatedDepartment);
        } catch (ResourceNotFound e){
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().build();
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<DepartmentDTO> updateDepartment(
            @PathVariable Long id,
            @RequestBody @Valid DepartmentDTO departmentDTO){
        try{
            DepartmentDTO updateDepartment = departmentService.updateDepartmentName(id, departmentDTO);
            return ResponseEntity.ok(updateDepartment);
        } catch (ResourceNotFound e){
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().build();
        } catch (ConflictException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Department> deleteDepartment(@PathVariable Long id){
        try {
            departmentService.deleteDepartment(id);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFound e){
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e){;
            return ResponseEntity.badRequest().build();
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
