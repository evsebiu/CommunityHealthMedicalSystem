package com.example.CommunityHealthMedicalSystem.Service;

import com.example.CommunityHealthMedicalSystem.Exception.DuplicateResourceException;
import com.example.CommunityHealthMedicalSystem.Exception.IllegalArgumentException;
import com.example.CommunityHealthMedicalSystem.Exception.ResourceNotFound;
import com.example.CommunityHealthMedicalSystem.Model.Patient;
import com.example.CommunityHealthMedicalSystem.Repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PatientServiceImpl implements PatientService{
    @Autowired
    private final PatientRepository patientRepo;
    public PatientServiceImpl(PatientRepository patientRepo){
        this.patientRepo = patientRepo;
    }

    @Override
    public Optional<Patient> findById(Long id){
        if (id == null){
            throw new IllegalArgumentException("Patient ID cannot be null.");
        }
        return patientRepo.findById(id);
    }

    @Override
    public List<Patient> findByFirstName(String firstName){
        if(firstName == null){
            throw new IllegalArgumentException("Patient first name is a required field.");
        }
        return patientRepo.findByFirstNameContainsIgnoreCase(firstName);
    }

    @Override
    public List<Patient> findByLastName(String lastName){
        if (lastName == null){
            throw new IllegalArgumentException("Patient last name is required.");
        }
        return patientRepo.findByLastNameContainsIgnoreCase(lastName);
    }

    @Override
    public Optional<Patient> findByEmail(String email){
        if (email == null){
            throw new IllegalArgumentException("Email is a required field. It cannot be null.");
        }
        return patientRepo.findByEmail(email);
    }

    @Override
    public Optional<Patient> findByPhoneNumber(String phoneNumber){
        if (phoneNumber == null){
            throw new IllegalArgumentException("Patient's phone number is required.");
        }
        return patientRepo.findByPhoneNumber(phoneNumber);
    }

    @Override
    public List<Patient> findByDateOfBirth(LocalDate dateOfBirth){
        if (dateOfBirth == null){
            throw new IllegalArgumentException("Patient's date of birth is required.");
        }
        return patientRepo.findByDateOfBirth(dateOfBirth);
    }

    @Override
    public List<Patient> findByAddressIgnoreCase(String address){
        if (address == null){
            throw new IllegalArgumentException("Patient address is required.");
        }
        return patientRepo.findByAddress(address);
    }

    @Override
    public Optional<Patient> findByNationalId(String nationalId){
        if (nationalId == null){
            throw new IllegalArgumentException("Patient national ID is required");
        }
        return patientRepo.findByNationalId(nationalId);
    }

    @Override
    public Patient createPatient(Patient patient){
        if (patient==null){
            throw new IllegalArgumentException("Patient details are requied.");
        }
        if (patient.getNationalId() != null && patientRepo.findByNationalId(patient.getNationalId())
                .isPresent() ){
            throw new DuplicateResourceException("Patient already exists in database.");
        }
        if (patient.getPhoneNumber() != null && patientRepo.findByPhoneNumber(patient.getPhoneNumber())
                .isPresent()){
            throw new DuplicateResourceException("Patient phone number already exists in database");
        }
        if (patient.getEmail() != null && patientRepo.findByEmail(patient.getEmail())
                .isPresent()){
            throw new DuplicateResourceException("Patient email alreadt exists in database.");
        }
        return patientRepo.save(patient);
    }



    @Override
    public void deletePatient(Long id){
        if (id == null){
            throw new IllegalArgumentException("Patient ID is required.");
        }

        Patient patient = patientRepo.findById(id)
                        .orElseThrow(()-> new ResourceNotFound("Patient does not exists in database"));
        patientRepo.delete(patient);
    }

    @Override
    public Patient updatePatient(Long id, Patient patientDetails){
        if (id == null){
            throw new IllegalArgumentException("Patient ID cannot be null");
        }
        if (patientDetails == null){
            throw new IllegalArgumentException("Patient detials are required for update.");
        }
        Patient existingPatient = patientRepo.findById(id)
                .orElseThrow(()-> new ResourceNotFound("Patiend not found in database"));

        if (patientDetails.getEmail() != null &&
        !patientDetails.getEmail().equals(existingPatient.getEmail())){
            patientRepo.findByEmail(patientDetails.getEmail())
                    .ifPresent(s->{
                        throw new DuplicateResourceException("Email " + patientDetails.getEmail()
                        + " already exits in database");
                    });
            }
        existingPatient.setEmail(patientDetails.getEmail());


        if (patientDetails.getNationalId() != null &&
        !patientDetails.getNationalId().equals(existingPatient.getNationalId())) {
            patientRepo.findByNationalId(patientDetails.getNationalId())
                    .ifPresent(s -> {
                        throw new DuplicateResourceException("Patient national ID " +
                                patientDetails.getNationalId() + " already exists in database.");
                    });
        }
        if (patientDetails.getPhoneNumber() != null &&
        !patientDetails.getPhoneNumber().equals(existingPatient.getPhoneNumber())){
            patientRepo.findByPhoneNumber(patientDetails.getPhoneNumber())
                    .ifPresent(s->{
                        throw new DuplicateResourceException("Patient with phone number "
                        + patientDetails.getPhoneNumber() + " already exists in system.");
                    });
            }
        existingPatient.setPhoneNumber(patientDetails.getPhoneNumber());

        if (patientDetails.getFirstName() !=null){
            existingPatient.setFirstName(patientDetails.getFirstName());
        }
        if (patientDetails.getLastName() != null){
            existingPatient.setLastName(patientDetails.getLastName());
        }
        if (patientDetails.getAddress() != null){
            existingPatient.setAddress(patientDetails.getAddress());
        }
        return patientRepo.save(existingPatient);
    }
}
