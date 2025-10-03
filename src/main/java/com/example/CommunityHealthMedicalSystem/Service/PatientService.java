package com.example.CommunityHealthMedicalSystem.Service;

import com.example.CommunityHealthMedicalSystem.DTO.PatientDTO;
import com.example.CommunityHealthMedicalSystem.Model.Appointment;
import com.example.CommunityHealthMedicalSystem.Model.MedicalStaff;
import com.example.CommunityHealthMedicalSystem.Model.Patient;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public interface PatientService {
    Optional<Patient> findById(Long id);
    List<Patient> findByFirstName(String firstName);
    List<Patient> findByLastName(String lastName);
    Optional<Patient> findByEmail(String email);
    Optional<Patient> findByPhoneNumber(String phoneNumber);
    Optional<Patient> findByNationalId(String nationalId);
    List<Patient> findByDateOfBirth(LocalDate dateOfBirth);
    List<Patient> findByAddressIgnoreCase(String address);

    PatientDTO createPatient(PatientDTO patientDTO);
    void deletePatient (Long id);
    PatientDTO updatePatient(PatientDTO patientDTO);

}
