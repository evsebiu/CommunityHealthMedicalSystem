package com.example.CommunityHealthMedicalSystem.Service;

import com.example.CommunityHealthMedicalSystem.DTO.MedicalRecordDTO;
import com.example.CommunityHealthMedicalSystem.Model.MedicalRecord;
import com.example.CommunityHealthMedicalSystem.Model.MedicalStaff;
import com.example.CommunityHealthMedicalSystem.Model.Patient;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public interface MedicalRecordService {
    List<MedicalRecord> getAllMedicalRecords();

    Optional<MedicalRecord> getMedicalRecordById(Long id);

    List<MedicalRecord> findByDiagnosisIgnoreCase(String diagnosis);

    List<MedicalRecord> findByPrescriptionIgnoreCase(String prescription);

    List<MedicalRecord> findByNotesIgnoreCase(String notes);

    List<MedicalRecord> findByRecordDate(LocalDate recordDate);

    List<MedicalRecord> getMedicalRecordByMedicalStaff(MedicalStaff medicalStaff);

    List<MedicalRecord> getMedicalRecordByDateRange(LocalDate startDate, LocalDate endDate);

    List<MedicalRecord> getMedicalRecordByPatient(Patient patient);

    List<MedicalRecord> getMedicalRecordByPatientId(Long patientId);

    MedicalRecordDTO createMedicalRecord(MedicalRecordDTO medicalRecordDTO);

    void deleteMedicalRecord(Long id, Long medicalStaffId);

    MedicalRecordDTO updateMedicalRecord(Long id,MedicalRecordDTO medicalRecordDTO);
}
