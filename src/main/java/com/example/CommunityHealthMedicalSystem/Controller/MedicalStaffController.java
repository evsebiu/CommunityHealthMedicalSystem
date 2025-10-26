package com.example.CommunityHealthMedicalSystem.Controller;


import com.example.CommunityHealthMedicalSystem.DTO.MedicalRecordDTO;
import com.example.CommunityHealthMedicalSystem.DTO.MedicalStaffDTO;
import com.example.CommunityHealthMedicalSystem.Exception.ConflictException;
import com.example.CommunityHealthMedicalSystem.Exception.DuplicateResourceException;
import com.example.CommunityHealthMedicalSystem.Exception.IllegalArgumentException;
import com.example.CommunityHealthMedicalSystem.Exception.ResourceNotFound;
import com.example.CommunityHealthMedicalSystem.Model.MedicalRecord;
import com.example.CommunityHealthMedicalSystem.Model.MedicalStaff;
import com.example.CommunityHealthMedicalSystem.Service.MedicalRecordServiceImpl;
import com.example.CommunityHealthMedicalSystem.Service.MedicalStaffServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/medicalStaff")
public class MedicalStaffController {
    private final MedicalStaffServiceImpl medicalStaffService;

    MedicalStaffController(MedicalStaffServiceImpl medicalStaffService){
        this.medicalStaffService = medicalStaffService;
    }

    @GetMapping
    public List<MedicalStaff> getAllMedicalStaff(){
        return medicalStaffService.getAllMedicalStaff();
    }

    @GetMapping("/{medicalStaffId}")
    public ResponseEntity <MedicalStaff> getMedicalStaffById(@PathVariable Long medicalStaffId){
        try {
            Optional<MedicalStaff> medicalStaff = medicalStaffService.getMedicalStaffById(medicalStaffId);
            return medicalStaff.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (ResourceNotFound e){
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/by-department/{departmentId}")
    public ResponseEntity<List<MedicalStaff>> getMedicalStaffByDepartmentId(@PathVariable Long departmentId){
        List<MedicalStaff> staffs = medicalStaffService.getByDepartment(departmentId);
        return staffs.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(staffs);
    }

    @PostMapping
    public ResponseEntity<MedicalStaffDTO> createMedicalStaff(@RequestBody MedicalStaffDTO medicalStaffDTO){
        try{
            MedicalStaffDTO savedStaff = medicalStaffService.createStaff(medicalStaffDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedStaff);
        } catch (DuplicateResourceException e){
            return ResponseEntity.badRequest().build();
        } catch(ResourceNotFound e){
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<MedicalStaffDTO> updateMedicalStaff(@PathVariable Long id,
                                                              @RequestBody MedicalStaffDTO medicalStaffDTO){
        try{
            MedicalStaffDTO updatedStaff = medicalStaffService.updateStaff(id, medicalStaffDTO);
            return ResponseEntity.ok(updatedStaff);
        } catch (ResourceNotFound e){
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().build();
        } catch (ConflictException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMedicalStaff(@PathVariable Long id){
        try {
            medicalStaffService.deleteStaff(id);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFound e){
            return ResponseEntity.notFound().build();
        } catch (SecurityException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

}
