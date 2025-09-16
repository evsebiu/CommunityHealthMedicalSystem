package com.example.CommunityHealthMedicalSystem.Service;

import com.example.CommunityHealthMedicalSystem.Exception.DuplicateResourceException;
import com.example.CommunityHealthMedicalSystem.Exception.IllegalArgumentException;
import com.example.CommunityHealthMedicalSystem.Exception.ResourceNotFound;
import com.example.CommunityHealthMedicalSystem.Model.Department;
import com.example.CommunityHealthMedicalSystem.Model.MedicalStaff;
import com.example.CommunityHealthMedicalSystem.Repository.MedicalStaffRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MedicalStaffServiceImpl implements MedicalStaffService {

    private final MedicalStaffRepository medicalStaffRepo;

    MedicalStaffServiceImpl(MedicalStaffRepository medicalStaffRepo) {
        this.medicalStaffRepo = medicalStaffRepo;
    }

    @Override
    public List<MedicalStaff> getByFirstNameIgnoreCase(String firstName) {
        if (firstName == null) {
            throw new IllegalArgumentException("First name field is required.");
        }
        return medicalStaffRepo.findByFirstNameContainsIgnoreCase(firstName);
    }

    @Override
    public List<MedicalStaff> getByLastNameIgnoreCase(String lastName) {
        if (lastName == null) {
            throw new IllegalArgumentException("Last name field is required.");
        }
        return medicalStaffRepo.findByLastNameContainsIgnoreCase(lastName);
    }

    @Override
    public Optional<MedicalStaff> getByEmailIgnoreCase(String email) {
        if (email == null) {
            throw new IllegalArgumentException("Email is a required field.");
        }

        return medicalStaffRepo.findByEmailContainsIgnoreCase(email);
    }

    @Override
    public Optional<MedicalStaff> getByLicenseNumber(String licenseNumber) {
        if (licenseNumber == null) {
            throw new IllegalArgumentException("License number cannot be null.");
        }
        return medicalStaffRepo.findByLicenseNumber(licenseNumber);
    }

    @Override
    public List<MedicalStaff> getBySpecializationIgnoreCase(String specialization) {
        if (specialization == null) {
            throw new IllegalArgumentException("Specialization must be declared.");
        }
        return medicalStaffRepo.findBySpecializationContainsIgnoreCase(specialization);
    }

    @Override
    public List<MedicalStaff> getByRoleIgnoreCase(MedicalStaff.Role role) {
        if (role == null) {
            throw new IllegalArgumentException("Role is a required field, it cannot be null.");
        }
        return medicalStaffRepo.findByRoleContains(role);
    }

    @Override
    public List<MedicalStaff> getByDepartment(Long departmentId) {
        if (departmentId == null) {
            throw new IllegalArgumentException("Department ID cannot be null");
        }
        return medicalStaffRepo.findByDepartmentId(departmentId);
    }

    @Override
    public Optional<MedicalStaff> getMedicalStaffById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        return medicalStaffRepo.findById(id);
    }

    @Override
    public MedicalStaff createStaff(MedicalStaff medicalStaff) {
        if (medicalStaff == null) {
            throw new IllegalArgumentException("Medical Staff details are requierd.");
        }


        if (medicalStaff.getEmail() != null && medicalStaffRepo.findByEmailContainsIgnoreCase(
                medicalStaff.getEmail()).isPresent()) {
            throw new DuplicateResourceException("Error! This person already exits in database.");
        }
        if (medicalStaff.getLicenseNumber() != null && medicalStaffRepo.findByLicenseNumber(
                medicalStaff.getLicenseNumber()).isPresent()) {
            throw new DuplicateResourceException("A staff member with this License Number already" +
                    "exists in database.");
        }
        return medicalStaffRepo.save(medicalStaff);
    }

    @Override
    public MedicalStaff updateStaff(Long id, MedicalStaff medicalStaffDetails, Department department) {
        if (id == null) {
            throw new IllegalArgumentException("Personal ID cannot be null.");
        }
        if (medicalStaffDetails == null) {
            throw new IllegalArgumentException("Medical Staff field cannot be null.");
        }
        if (department == null) {
            throw new IllegalArgumentException("Department cannot be null before update," +
                    " create medical staff first.");
        }

        MedicalStaff existingStaff = medicalStaffRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFound("Medical Personal with ID: " + id + " not found."));

        if (medicalStaffDetails.getEmail() != null &&
                !medicalStaffDetails.getEmail().equals(existingStaff.getEmail())) {
            medicalStaffRepo.findByEmailContainsIgnoreCase(medicalStaffDetails.getEmail())
                    .ifPresent(s -> {
                        throw new DuplicateResourceException("Email '" + medicalStaffDetails.getEmail() +
                                "' is already in use by another staff member.");
                    });
            existingStaff.setEmail(medicalStaffDetails.getEmail());


            if (medicalStaffDetails.getLicenseNumber() != null &&
                    !medicalStaffDetails.getLicenseNumber().equals(existingStaff.getLicenseNumber())) {
                medicalStaffRepo.findByLicenseNumber(medicalStaffDetails.getLicenseNumber())
                        .ifPresent(s -> {
                            throw new DuplicateResourceException("License number " + medicalStaffDetails.getLicenseNumber() +
                                    "is already in use by another staff member");
                        });
            }
            if (medicalStaffDetails.getFirstName() != null) {
                medicalStaffDetails.setFirstName(medicalStaffDetails.getFirstName());
            }
            if (medicalStaffDetails.getLastName() != null) {
                medicalStaffDetails.setLastName(medicalStaffDetails.getLastName());
            }
            if (medicalStaffDetails.getSpecialization() != null) {
                medicalStaffDetails.setSpecialization(medicalStaffDetails.getSpecialization());
            }
            if (medicalStaffDetails.getRole() != null) {
                medicalStaffDetails.setRole(medicalStaffDetails.getRole());
            }
        }
        return medicalStaffRepo.save(medicalStaffDetails);
    }
    @Override
    public void deleteStaff(Long id, MedicalStaff deletingStaff){
        if (deletingStaff == null){
            throw new IllegalArgumentException("Deleting staff cannot be null");
        }
        if (id == null){
            throw new IllegalArgumentException("ID cannot be a null field.");
        }
        MedicalStaff staffToDelete = medicalStaffRepo.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("Medical Staff cannot be found based on id."));

        if (staffToDelete.getId().equals(deletingStaff.getId())){
            throw new IllegalArgumentException("Staff cannot delete their own profile.");
        }
        medicalStaffRepo.delete(staffToDelete);
    }
}
