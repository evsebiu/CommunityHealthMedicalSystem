package com.example.CommunityHealthMedicalSystem.Repository;

import com.example.CommunityHealthMedicalSystem.Model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient,Long> {
    List<Patient> findByFirstNameContainsIgnoreCase(String firstName);
    List<Patient> findByLastNameContainsIgnoreCase(String lastName);
    Optional<Patient> findByEmail(String email);
    Optional<Patient> findByPhoneNumber(String phoneNumber);
    Optional<Patient> findByNationalId(String nationalId);
    List<Patient> findByDateOfBirth(LocalDate dateOfBirth);
    List<Patient> findByAddress(String address);
    boolean existsByEmailAndIdNot(String email, Long id);
    boolean existsByNationalIdAndIdNot(String nationalId, Long id);

}
