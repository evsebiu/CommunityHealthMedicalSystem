package com.example.CommunityHealthMedicalSystem.Repository;

import com.example.CommunityHealthMedicalSystem.Model.MedicalRecord;
import com.example.CommunityHealthMedicalSystem.Model.MedicalStaff;
import com.example.CommunityHealthMedicalSystem.Model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {
    List<MedicalRecord> findByDiagnosisIgnoreCase(String diagnosis);
    List<MedicalRecord> findByPrescription(String prescription);
    List<MedicalRecord> findByNotes(String notes);
    List<MedicalRecord> findByRecordDate(LocalDate recordDate);
    List<MedicalRecord> findByMedicalStaff(MedicalStaff medicalStaff);
    List<MedicalRecord> findByRecordDateBetween(LocalDate startDate, LocalDate endDate);
    List<MedicalRecord> findMedicalRecordByPatient(Patient patient);
    List<MedicalRecord> findMedicalRecordByPatientId(Long patientId);
    boolean existsByPatientAndRecordDate(Patient patient, LocalDate recordDate);
}
