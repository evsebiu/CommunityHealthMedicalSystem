package com.example.CommunityHealthMedicalSystem.Service;


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
        return medicalRecordRepo.findByDiagnosis(diagnosis);
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
        return medicalRecordRepo.findMedicalRecordByPatiendId(patientId);
    }

    @Override
    public MedicalRecord createMedicalRecord(MedicalRecord medicalRecord, Patient patient, MedicalStaff medicalStaff){
        if (medicalRecord == null){
            throw new IllegalArgumentException("Medical record is required, it cannot be null.");
        }
        if (patient == null){
            throw new IllegalArgumentException("Patient field is required, it cannot be null.");
        }
        if (medicalStaff == null){
            throw new IllegalArgumentException("Medical staff is a required field, it cannot be null.");
        }

        Optional<MedicalRecord> existInDatabase = medicalRecordRepo.findById(medicalRecord.getId());
        if (existInDatabase.isPresent()){
            throw new DuplicateResourceException("Medical record already exist in database, it cannot overwrite.");
        }

        Patient existstingPatient = patientRepo.findById(patient.getId())
                        .orElseThrow(()-> new ResourceNotFound("Patient not found."));
        MedicalStaff existingStaff = medicalStaffRepo.findById(medicalStaff.getId())
                        .orElseThrow(()->new ResourceNotFound("Medical Staff not found."));

        medicalRecord.setPatient(patient);
        medicalRecord.setMedicalStaff(medicalStaff);
        medicalRecord.setRecordDate(LocalDate.now());

        if (isDuplicateMedicalRecord(medicalRecord)){
            throw new DuplicateResourceException("Same medical records already exits for this patient.");
        }

        MedicalRecord savedMedicalRecord = medicalRecordRepo.save(medicalRecord);
        System.out.println("Medical Record saved for " + patient.getFirstName() + " " + patient.getLastName() +
                " " + "with Medical Staff " + medicalStaff.getLastName() + " department " + medicalStaff.getDepartment() +
                " specialization " + medicalStaff.getSpecialization());
        return savedMedicalRecord;
    }

    //helper method to check duplicate records
    private boolean isDuplicateMedicalRecord(MedicalRecord medicalRecord){
        return medicalRecordRepo.exitsByPatientAndRecordDate(medicalRecord.getPatient(), medicalRecord.getRecordDate());
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
    public MedicalRecord updateMedicalRecord(Long id, MedicalRecord medicalRecordDetails ,MedicalStaff updatingStaff){
        if (updatingStaff == null){
            throw new IllegalArgumentException("Updating Medical Staff is a required field, it cannot be null.");
        }

        MedicalRecord existingRecord = medicalRecordRepo.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("Medical record not found."));

        if (medicalRecordDetails.getDiagnosis()!= null){
            existingRecord.setDiagnosis(medicalRecordDetails.getDiagnosis());
        }
        if (medicalRecordDetails.getNotes() != null){
            existingRecord.setNotes(medicalRecordDetails.getNotes());
        }
        if (medicalRecordDetails.getPrescription() != null){
                existingRecord.setPrescription(medicalRecordDetails.getPrescription());
        }

        if (medicalRecordDetails.getRecordDate() != null){
            existingRecord.setRecordDate(medicalRecordDetails.getRecordDate());
        }
        return medicalRecordRepo.save(existingRecord);
    }
}
