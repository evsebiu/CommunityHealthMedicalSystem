package com.example.CommunityHealthMedicalSystem.Repository;

import com.example.CommunityHealthMedicalSystem.Model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient,Long> {
    List<Patient> findByFirstNameContainingIgnoreCase(String firstName);
    List<Patient> findByLastNameContainingIgnoreCase(String lastName);
    List<Patient> findByEmailContainingIgnoreCase(String email);
    Optional<Patient> findByPhoneNumber(String phoneNumber);
    Optional<Patient> findByNationalId(String nationalId);
    List<Patient> findByDateOfBirth(LocalDate dateOfBirth);
    List<Patient> findByAddressContainingIgnoreCase(String address);
    boolean existsByEmailAndIdNot(String email, Long id);
    boolean existsByNationalIdAndIdNot(String nationalId, Long id);
    @Query("SELECT p FROM Patient p WHERE " +
            "(:firstName IS NULL OR LOWER(p.firstName) LIKE LOWER(CONCAT('%', :firstName, '%'))) AND " +
            "(:lastName IS NULL OR LOWER(p.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))) AND " +
            "(:email IS NULL OR LOWER(p.email) LIKE LOWER(CONCAT('%', :email, '%'))) AND " +
            "(:address IS NULL OR LOWER(p.address) LIKE LOWER(CONCAT('%', :address, '%')))")
    List<Patient> searchPatients(@Param("firstName") String firstName,
                                 @Param("lastName") String lastName,
                                 @Param("email") String email,
                                 @Param("address") String address);
}
