package com.example.CommunityHealthMedicalSystem.Controller;

import com.example.CommunityHealthMedicalSystem.DTO.MedicalRecordDTO;
import com.example.CommunityHealthMedicalSystem.Exception.ConflictException;
import com.example.CommunityHealthMedicalSystem.Exception.DuplicateResourceException;
import com.example.CommunityHealthMedicalSystem.Exception.IllegalArgumentException;
import com.example.CommunityHealthMedicalSystem.Exception.ResourceNotFound;
import com.example.CommunityHealthMedicalSystem.Model.MedicalRecord;
import com.example.CommunityHealthMedicalSystem.Model.MedicalStaff;
import com.example.CommunityHealthMedicalSystem.Repository.MedicalRecordRepository;
import com.example.CommunityHealthMedicalSystem.Service.MedicalRecordService;
import com.example.CommunityHealthMedicalSystem.Service.MedicalRecordServiceImpl;
import com.example.CommunityHealthMedicalSystem.Service.MedicalStaffServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.annotation.Retention;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/medicalRecords")
public class MedicalRecordController {

    private final MedicalRecordServiceImpl medicalService;
    private final MedicalRecordRepository medicalRepo;
    private final MedicalStaffServiceImpl medicalStaffService;

    public MedicalRecordController(MedicalRecordServiceImpl medicalService,
                                   MedicalStaffServiceImpl medicalStaffService,
                                   MedicalRecordRepository medicalRepo) {
        this.medicalService = medicalService;
        this.medicalRepo = medicalRepo;
        this.medicalStaffService = medicalStaffService;
    }

    @GetMapping
    public List<MedicalRecord> getAllMedicalRecords() {
        return medicalService.getAllMedicalRecords();
    }

    @GetMapping({"/{id}"})
    public ResponseEntity<MedicalRecord> getMedicalRecordById(@PathVariable Long id) {
        try {
            Optional<MedicalRecord> medicalRecord = medicalService.getMedicalRecordById(id);
            return medicalRecord.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("/by-patient/{patientId}")
    public ResponseEntity<List<MedicalRecord>> getMedicalRecordByPatient(@PathVariable Long patientId){
        List<MedicalRecord> medicalRecords = medicalService.getMedicalRecordByPatientId(patientId);
        if (medicalRecords.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(medicalRecords);
    }

    @PostMapping
    public ResponseEntity<MedicalRecordDTO> createMedicalRecord(@RequestBody @Valid MedicalRecordDTO medicalRecordDTO){ // FIXED: Added @Valid
        try{
            MedicalRecordDTO savedMedicalRecord = medicalService.createMedicalRecord(medicalRecordDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedMedicalRecord);
        } catch (DuplicateResourceException e){
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<MedicalRecordDTO> updateMedicalRecord(@PathVariable Long id,
                                                                @RequestBody @Valid MedicalRecordDTO medicalRecordDTO){
        try{
            MedicalRecordDTO updatedMedicalRecord = medicalService.updateMedicalRecord(id, medicalRecordDTO);
            return ResponseEntity.ok(updatedMedicalRecord);
        } catch (ResourceNotFound e){
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().build();
        } catch (ConflictException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMedicalRecord(@PathVariable Long id,
                                                    @RequestParam Long medicalStaffId){
        try{
            medicalService.deleteMedicalRecord(id, medicalStaffId);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFound e){
            return ResponseEntity.notFound().build();
        } catch (SecurityException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}