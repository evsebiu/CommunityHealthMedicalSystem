package com.example.CommunityHealthMedicalSystem.Controller;

import com.example.CommunityHealthMedicalSystem.DTO.PatientDTO;
import com.example.CommunityHealthMedicalSystem.Exception.ConflictException;
import com.example.CommunityHealthMedicalSystem.Exception.IllegalArgumentException;
import com.example.CommunityHealthMedicalSystem.Exception.ResourceNotFound;
import com.example.CommunityHealthMedicalSystem.Model.Patient;
import com.example.CommunityHealthMedicalSystem.Service.PatientServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/patients")
@Validated
public class PatientController {

    private final PatientServiceImpl patientService;

    public PatientController(PatientServiceImpl patientService){
        this.patientService = patientService;
    }

    @GetMapping
    public List<Patient> getAllPatients(){
        return patientService.getAllPatients();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Patient> getPatientById(@PathVariable Long id){
        try{
            Optional<Patient> patient = patientService.findById(id);
            return patient.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<PatientDTO> createPatient(@Valid @RequestBody PatientDTO patientDTO){ // Added @Valid here
        try {
            PatientDTO savedPatient = patientService.createPatient(patientDTO);
            return ResponseEntity.ok(savedPatient);
        } catch (ConflictException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<PatientDTO> updatePatient(@PathVariable Long id,
                                                    @Valid @RequestBody PatientDTO patientDTO){
        try {
            PatientDTO updatePatient = patientService.updatePatient(id, patientDTO);
            return ResponseEntity.ok(updatePatient);
        } catch (ResourceNotFound e){
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().build();
        } catch (ConflictException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id){
        try{
            patientService.deletePatient(id);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFound e){
            return ResponseEntity.notFound().build();
        } catch (SecurityException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<Patient>> searchPatients(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String address){
        List<Patient> patients = patientService.searchPatients(firstName,lastName, email, address);
        return ResponseEntity.ok(patients);
    }
}