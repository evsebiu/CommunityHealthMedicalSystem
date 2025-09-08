package com.example.CommunityHealthMedicalSystem.Repository;

import com.example.CommunityHealthMedicalSystem.Model.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {
    List<MedicalRecord> findByDiagnosis(String diagnosis);
    List<MedicalRecord> findByPrescription(String prescription);
    List<MedicalRecord> findByNotes(String notes);
    List<MedicalRecord> findByRecordDate(LocalDate recordDate);
}
