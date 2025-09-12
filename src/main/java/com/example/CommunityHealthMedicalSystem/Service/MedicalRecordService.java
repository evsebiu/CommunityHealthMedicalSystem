package com.example.CommunityHealthMedicalSystem.Service;

import com.example.CommunityHealthMedicalSystem.Model.MedicalRecord;
import com.example.CommunityHealthMedicalSystem.Model.MedicalStaff;
import com.example.CommunityHealthMedicalSystem.Model.Patient;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public interface MedicalRecordService {
    Optional<MedicalRecord> getMedicalRecordById(Long id);
    List<MedicalRecord> findByDiagnosisIgnoreCase(String diagnosis);
    List<MedicalRecord> findByPrescriptionIgnoreCase(String prescription);
    List<MedicalRecord> findByNotesIgnoreCase(String notes);
    List<MedicalRecord> findByRecordDate(LocalDate recordDate);
    List<MedicalRecord> getMedicalRecordByMedicalStaff(MedicalStaff medicalStaff);
    List<MedicalRecord> getMedicalRecordByDateRange(LocalDate startDate, LocalDate endDate);

    List<MedicalRecord> getMedicalRecordByPatient(Patient patient);
    List<MedicalRecord> getMedicalRecordByPatientId(Long patientId);

    MedicalRecord createMedicalRecord(MedicalRecord medicalRecord, Patient patient, MedicalStaff medicalStaff);
    void deleteMedicalRecord(Long id, MedicalStaff deletingStaff);
    MedicalRecord updateMedicalRecord(Long id, MedicalRecord medicalRecordDetails, MedicalStaff updatingStaff);
}
