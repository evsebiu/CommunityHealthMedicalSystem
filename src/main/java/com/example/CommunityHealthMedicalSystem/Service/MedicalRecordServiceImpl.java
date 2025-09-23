package com.example.CommunityHealthMedicalSystem.Service;


import com.example.CommunityHealthMedicalSystem.DTO.MedicalRecordDTO;
import com.example.CommunityHealthMedicalSystem.Exception.DuplicateResourceException;
import com.example.CommunityHealthMedicalSystem.Exception.IllegalArgumentException;
import com.example.CommunityHealthMedicalSystem.Exception.ResourceNotFound;
import com.example.CommunityHealthMedicalSystem.Exception.SecurityException;
import com.example.CommunityHealthMedicalSystem.Model.MedicalRecord;
import com.example.CommunityHealthMedicalSystem.Model.MedicalStaff;
import com.example.CommunityHealthMedicalSystem.Model.Patient;
import com.example.CommunityHealthMedicalSystem.Repository.MedicalRecordRepository;
import com.example.CommunityHealthMedicalSystem.Repository.MedicalStaffRepository;
import com.example.CommunityHealthMedicalSystem.Repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class MedicalRecordServiceImpl implements MedicalRecordService{

    @Autowired
    private final PatientRepository patientRepo;
    @Autowired
    private final MedicalRecordRepository medicalRecordRepo;
    @Autowired
    private final MedicalStaffRepository medicalStaffRepo;
    public MedicalRecordServiceImpl(PatientRepository patientRepo, MedicalRecordRepository medicalRecordRepo,
                                    MedicalStaffRepository medicalStaffRepo){
        this.medicalRecordRepo=medicalRecordRepo;
        this.patientRepo = patientRepo;
        this.medicalStaffRepo=medicalStaffRepo;
    }

    @Override
    public Optional<MedicalRecord> getMedicalRecordById(Long id){
        if (id == null){
            throw new IllegalArgumentException("Medical record ID cannot be null!");
        }
        return medicalRecordRepo.findById(id);
    }

    @Override
    public List<MedicalRecord> findByDiagnosisIgnoreCase(String diagnosis){
        if (diagnosis == null){
            throw new IllegalArgumentException("Patient diagnosis cannot be null!");
        }
        return medicalRecordRepo.findByDiagnosisIgnoreCase(diagnosis);
    }

    @Override
    public List<MedicalRecord> findByPrescriptionIgnoreCase(String prescription){
        if (prescription == null){
            throw new IllegalArgumentException("Patient prescription cannot be null!");
        }
        return medicalRecordRepo.findByPrescription(prescription);
    }

    @Override
    public List<MedicalRecord> findByNotesIgnoreCase(String notes){
        if (notes == null){
            throw new IllegalArgumentException("Patient notes are required!");
        }
        return medicalRecordRepo.findByNotes(notes);
    }

    @Override
    public List<MedicalRecord> findByRecordDate(LocalDate recordDate){
        if (recordDate == null ){
            throw new IllegalArgumentException("Record date is required, it cannot be null.");
        }

        return medicalRecordRepo.findByRecordDate(recordDate);
    }

    @Override
    public List<MedicalRecord> getMedicalRecordByMedicalStaff(MedicalStaff medicalStaff){
        if (medicalStaff == null){
            throw new IllegalArgumentException("Medical staff cannot be null, it's required.");
        }
        return medicalRecordRepo.findByMedicalStaff(medicalStaff);
    }

    @Override
    public List<MedicalRecord> getMedicalRecordByDateRange(LocalDate startDate, LocalDate endDate){
        if (startDate == null){
            throw new IllegalArgumentException("Start date of medical record cannot be null.");
        }
        if (endDate == null){
            throw new IllegalArgumentException("End date of medical record cannot be null");
        }

        if (startDate.isAfter(endDate)){
            throw new IllegalArgumentException("Start date cannot be afte the end date.");
        }

        LocalDate today = LocalDate.now();
        if (startDate.isAfter(today) || endDate.isAfter(today)){
            throw new IllegalArgumentException("Dates cannot be in future");
        }

        return medicalRecordRepo.findByRecordDateBetween(startDate,endDate);
    }

    @Override
    public List<MedicalRecord> getMedicalRecordByPatient(Patient patient){
        if (patient == null){
            throw new IllegalArgumentException("Patient field is requierd.");
        }
        return medicalRecordRepo.findMedicalRecordByPatient(patient);
    }

    @Override
    public List<MedicalRecord> getMedicalRecordByPatientId(Long patientId){
        if (patientId == null){
            throw new IllegalArgumentException("Patiend ID is required to get Medical Records.");
        }
        return medicalRecordRepo.findMedicalRecordByPatientId(patientId);
    }

    @Override
    public MedicalRecordDTO createMedicalRecord(MedicalRecordDTO medicalRecordDTO){
        //1. validate inputs.
        if (medicalRecordDTO == null){
            throw new IllegalArgumentException("Medical record is required, it cannot be null.");
        }
        if (medicalRecordDTO.getPatientId() == null){
            throw new IllegalArgumentException("Patient field is required, it cannot be null.");
        }
        if (medicalRecordDTO.getMedicalStaffId() == null){
            throw new IllegalArgumentException("Medical staff is a required field, it cannot be null.");
        }
        if (medicalRecordDTO.getRecordDate() == null){
            throw new IllegalArgumentException("Record date is required.");
        }

        //2. check if patients exists.
        Patient patient = patientRepo.findById(medicalRecordDTO.getPatientId())
                .orElseThrow(()-> new ResourceNotFound("Patient with ID " + medicalRecordDTO.getPatientId() +
                                " does not exists in database."));

        //3. check if medical staff exists.
        MedicalStaff medicalStaff = medicalStaffRepo.findById(medicalRecordDTO.getMedicalStaffId())
                .orElseThrow(()-> new ResourceNotFound("Medical staff with ID" + medicalRecordDTO.getMedicalStaffId() +
                        " does not exists in database."));

        //4. check for duplicate medical records.
        if (medicalRecordRepo.existsByPatientAndRecordDate(patient, medicalRecordDTO.getRecordDate())){
            throw new DuplicateResourceException("Medical record with same details already exists.");
        }

        // 3. convert DTO to entity
        MedicalRecord medicalRecord = new MedicalRecord();
        medicalRecord.setPatient(patient);
        medicalRecord.setMedicalStaff(medicalStaff);
        medicalRecord.setNotes(medicalRecordDTO.getNotes());
        medicalRecord.setPrescription(medicalRecordDTO.getPrescription());
        medicalRecord.setDiagnosis(medicalRecordDTO.getDiagnosis());
        medicalRecord.setRecordDate(medicalRecordDTO.getRecordDate());

        MedicalRecord savedMedicalRecord = medicalRecordRepo.save(medicalRecord);
        return convertToDTO(savedMedicalRecord);
    }

    @Override
    public void deleteMedicalRecord(Long id, MedicalStaff deletingStaff){
        if (deletingStaff == null){
            throw new IllegalArgumentException("Deleting staff cannot be null. Field is required.");
        }

        MedicalRecord medicalRecord = medicalRecordRepo.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("Medical Record with id: " + id + " not found."));

        if(!medicalRecord.getMedicalStaff().getId().equals(deletingStaff.getId())){
            throw new SecurityException("Only authorised medical staff can do this operation");
        }
        medicalRecordRepo.delete(medicalRecord);
    }

    @Override
    public MedicalRecordDTO updateMedicalRecord(MedicalRecordDTO medicalRecordDTO){
        // 1. find existing medical record.
        MedicalRecord existingRecord = medicalRecordRepo.findById(medicalRecordDTO.getId())
                .orElseThrow(()-> new ResourceNotFound("Medical record with ID " + medicalRecordDTO.getId() +
                        " does not exists in database."));
        //2. validate input
        if (medicalRecordDTO == null){
            throw new IllegalArgumentException("Medical record cannot be null.");
        }

        //3. update fields from DTO
        if (medicalRecordDTO.getRecordDate() != null){
            if (!existingRecord.getRecordDate().equals(medicalRecordDTO.getRecordDate())){

                Patient patient = existingRecord.getPatient();
                if (medicalRecordDTO.getPatientId() != null){
                    patient = patientRepo.findById(medicalRecordDTO.getPatientId())
                            .orElse(existingRecord.getPatient());
                }
                existingRecord.setRecordDate(medicalRecordDTO.getRecordDate());
                MedicalStaff medicalStaff = existingRecord.getMedicalStaff();
                if (medicalRecordDTO.getMedicalStaffId() != null){
                    medicalStaff = medicalStaffRepo.findById(medicalRecordDTO.getMedicalStaffId())
                            .orElse(existingRecord.getMedicalStaff());
                }
            }
            if (medicalRecordDTO.getNotes() != null){
                existingRecord.setNotes(medicalRecordDTO.getNotes());
            }
            if (medicalRecordDTO.getDiagnosis() != null){
                existingRecord.setDiagnosis(medicalRecordDTO.getDiagnosis());
            }
            if (medicalRecordDTO.getPrescription() !=null){
                existingRecord.setPrescription(medicalRecordDTO.getPrescription());
            }

            // 4. handle relationship updates.
            if (medicalRecordDTO.getPatientId() != null){
                Patient patient = patientRepo.findById(medicalRecordDTO.getPatientId())
                        .orElseThrow(()-> new ResourceNotFound("Patient with ID: " + medicalRecordDTO.getPatientId() +
                                " not found."));
                existingRecord.setPatient(patient);
            }
            if(medicalRecordDTO.getMedicalStaffId() != null){
                MedicalStaff medicalStaff = medicalStaffRepo.findById(medicalRecordDTO.getMedicalStaffId())
                        .orElseThrow(()-> new ResourceNotFound("MedicalStaff with ID: " + medicalRecordDTO.getMedicalStaffId() +
                                "not found."));
                existingRecord.setMedicalStaff(medicalStaff);
            }
        }
        MedicalRecord updatedMedicalRecord = medicalRecordRepo.save(existingRecord);
        return convertToDTO(updatedMedicalRecord);
    }

    public MedicalRecordDTO convertToDTO (MedicalRecord medicalRecord){

        MedicalRecordDTO dto = new MedicalRecordDTO();

        dto.setId(medicalRecord.getId());
        dto.setDiagnosis(medicalRecord.getDiagnosis());
        dto.setMedicalStaffId(medicalRecord.getMedicalStaff().getId());
        dto.setRecordDate(medicalRecord.getRecordDate());
        dto.setPrescription(medicalRecord.getPrescription());
        dto.setNotes(medicalRecord.getNotes());
        dto.setPatientId(medicalRecord.getPatient().getId());

        return dto;
    }
}
