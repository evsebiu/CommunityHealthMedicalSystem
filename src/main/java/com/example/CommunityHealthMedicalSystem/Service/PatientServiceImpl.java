package com.example.CommunityHealthMedicalSystem.Service;

import com.example.CommunityHealthMedicalSystem.DTO.PatientDTO;
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


    private final PatientRepository patientRepo;
    public PatientServiceImpl(PatientRepository patientRepo){
        this.patientRepo = patientRepo;
    }


    @Override
    public List<Patient> getAllPatients(){
        return patientRepo.findAll();
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
        return patientRepo.findByFirstNameContainingIgnoreCase(firstName);
    }

    @Override
    public List<Patient> findByLastName(String lastName){
        if (lastName == null){
            throw new IllegalArgumentException("Patient last name is required.");
        }
        return patientRepo.findByLastNameContainingIgnoreCase(lastName);
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
        return patientRepo.findByAddressContainingIgnoreCase(address);
    }

    @Override
    public Optional<Patient> findByNationalId(String nationalId){
        if (nationalId == null){
            throw new IllegalArgumentException("Patient national ID is required");
        }
        return patientRepo.findByNationalId(nationalId);
    }

    @Override
    public PatientDTO createPatient(PatientDTO patientDTO){


        // input validations.
        if (patientDTO == null){
            throw new IllegalArgumentException("Patient's details are required.");
        }


        // check if email, phone number and national field from Patient class already exists in db.
        //if (patientDTO.getEmail() != null && patientRepo.findByEmail(patientDTO.getEmail()).isEmpty()){
           // throw new DuplicateResourceException("Error! This email already exists in database.");
        //}

        //if (patientDTO.getNationalId() != null && patientRepo.findByNationalId(patientDTO.getNationalId()).isPresent()){
          //  throw new DuplicateResourceException("Error! Patient's National ID already exists in database.");
       // }

       // if (patientDTO.getPhoneNumber()!= null && patientRepo.findByPhoneNumber(patientDTO.getPhoneNumber()).isPresent()){
         //   throw new IllegalArgumentException("Error! Patient's phone numver already exists in database.");
      //  }

        // convert DTO to entity

        Patient patient  = new Patient();
        patient.setAddress(patientDTO.getAddress());
        patient.setEmail(patientDTO.getEmail());
        patient.setNationalId(patientDTO.getNationalId());
        patient.setLastName(patientDTO.getLastName());
        patient.setFirstName(patientDTO.getFirstName());
        patient.setDateOfBirth(patientDTO.getDateOfBirth());
        patient.setPhoneNumber(patientDTO.getPhoneNumber());


        //save and return.

        Patient newPatient = patientRepo.save(patient);
        return convertToDTO(newPatient);

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
    public PatientDTO updatePatient(Long id, PatientDTO patientDTO){
        // 1. find existing Patient.
        Patient existingPatient = patientRepo.findById(id)
                .orElseThrow(()-> new ResourceNotFound("Patient not found."));

        //2. validate Input
        if (patientDTO == null){
            throw new IllegalArgumentException("Patient cannot be null.");
        }

        //3. check if patient's email exists.
        if (!existingPatient.getEmail().equals(patientDTO.getEmail())){
            boolean emailExists = patientRepo.existsByEmailAndIdNot(patientDTO.getEmail(), id);
            if (emailExists){
                throw new IllegalArgumentException("Email is already taken by another patient.");
            }
        }
        if (!existingPatient.getNationalId().equals(patientDTO.getNationalId())){
            boolean nationalExists = patientRepo.existsByNationalIdAndIdNot(patientDTO.getNationalId(), id);
            if (nationalExists){
                throw new IllegalArgumentException("National ID is owned by another patient.");
            }
        }

        // update only allowed fields.

        existingPatient.setFirstName(patientDTO.getFirstName());
        existingPatient.setLastName(patientDTO.getLastName());
        existingPatient.setEmail(patientDTO.getEmail());
        existingPatient.setPhoneNumber(patientDTO.getPhoneNumber());
        existingPatient.setAddress(patientDTO.getAddress());

        //save entity
        Patient updatedPatient = patientRepo.save(existingPatient);
        return convertToDTO(updatedPatient);

    }

    public PatientDTO convertToDTO(Patient patient){
        PatientDTO dto = new PatientDTO();
        dto.setId(patient.getId());
        dto.setAddress(patient.getAddress());
        dto.setEmail(patient.getEmail());
        dto.setDateOfBirth(patient.getDateOfBirth());
        dto.setNationalId(patient.getNationalId());
        dto.setFirstName(patient.getFirstName());
        dto.setLastName(patient.getLastName());
        dto.setPhoneNumber(patient.getPhoneNumber());

        return dto;
    }

    @Override
    public List<Patient> searchPatients(String firstName, String lastName, String email,
                                        String address){
        return patientRepo.searchPatients(firstName, lastName, email, address);
    }
}
