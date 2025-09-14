package com.example.CommunityHealthMedicalSystem.Service;

import com.example.CommunityHealthMedicalSystem.Exception.DuplicateResourceException;
import com.example.CommunityHealthMedicalSystem.Exception.IllegalArgumentException;
import com.example.CommunityHealthMedicalSystem.Model.Department;
import com.example.CommunityHealthMedicalSystem.Model.MedicalStaff;
import com.example.CommunityHealthMedicalSystem.Repository.MedicalStaffRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MedicalStaffServiceImpl implements MedicalStaffService{
    private final MedicalStaffRepository medicalStaffRepo;
    MedicalStaffServiceImpl(MedicalStaffRepository medicalStaffRepo){
        this.medicalStaffRepo=medicalStaffRepo;
    }

    @Override
    public List<MedicalStaff> getByFirstNameIgnoreCase(String firstName){
        if (firstName == null){
            throw new IllegalArgumentException("First name field is required.");
        }
        return medicalStaffRepo.findByFirstNameContainsIgnoreCase(firstName);
    }

    @Override
    public List<MedicalStaff> getByLastNameIgnoreCase(String lastName){
        if (lastName == null){
            throw new IllegalArgumentException("Last name field is required.");
        }
        return medicalStaffRepo.findByLastNameContainsIgnoreCase(lastName);
    }

    @Override
    public Optional<MedicalStaff> getByEmailIgnoreCase(String email){
        if (email == null){
            throw new IllegalArgumentException("Email is a required field.");
        }

        return medicalStaffRepo.findByEmailContainsIgnoreCase(email);
    }

    @Override
    public Optional<MedicalStaff> getByLicenseNumber(String licenseNumber){
        if (licenseNumber == null){
            throw new IllegalArgumentException("License number cannot be null.");
        }
        return medicalStaffRepo.findByLicenseNumber(licenseNumber);
    }
    @Override
    public List<MedicalStaff> getBySpecializationIgnoreCase(String specialization){
        if (specialization == null){
            throw new IllegalArgumentException("Specialization must be declared.");
        }
        return medicalStaffRepo.findBySpecializationContainsIgnoreCase(specialization);
    }

    @Override
    public List<MedicalStaff> getByRoleIgnoreCase(MedicalStaff.Role role){
        if (role == null){
            throw new IllegalArgumentException("Role is a required field, it cannot be null.");
        }
        return medicalStaffRepo.findByRoleContains(role);
    }

    @Override
    public List<MedicalStaff> getByDepartment(Long departmentId){
        if (departmentId == null){
            throw new IllegalArgumentException("Department ID cannot be null");
        }
        return medicalStaffRepo.findByDepartmentId(departmentId);
    }

    @Override
    public Optional<MedicalStaff> getMedicalStaffById(Long id){
        if (id == null){
            throw new IllegalArgumentException("ID cannot be null");
        }
        return medicalStaffRepo.findById(id);
    }

    @Override
    public MedicalStaff createStaff(MedicalStaff medicalStaff){
        if (medicalStaff == null){
            throw new IllegalArgumentException("Medical Staff details are requierd.");
        }

        //check if already exits in system
        Optional<MedicalStaff> exits = medicalStaffRepo.findById(medicalStaff.getId());
        if (exits.isPresent()){
            throw new DuplicateResourceException("Error! This person already exits in database.");
        }

        medicalStaff.setFirstName(medicalStaff.getFirstName());
        medicalStaff.setLastName(medicalStaff.getLastName());
        medicalStaff.setEmail(medicalStaff.getEmail());
        medicalStaff.setLicenseNumber(medicalStaff.getLicenseNumber());
        medicalStaff.setSpecialization(medicalStaff.getSpecialization());
        medicalStaff.setRole(medicalStaff.getRole());
        medicalStaff.setDepartment(medicalStaff.getDepartment());

        MedicalStaff savedMedicalStaff = medicalStaffRepo.save(medicalStaff);
        System.out.println("Medical Staff created with first name " + medicalStaff.getFirstName()
        + " last name " + medicalStaff.getLastName() + " email " + medicalStaff.getEmail()
        + " license number " + medicalStaff.getLicenseNumber() + " specialization " +
                medicalStaff.getSpecialization() + " role " + medicalStaff.getRole() +
                " at department " + medicalStaff.getDepartment());
        return savedMedicalStaff;

    }
}
