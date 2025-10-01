package com.example.CommunityHealthMedicalSystem.Service;

import com.example.CommunityHealthMedicalSystem.DTO.MedicalStaffDTO;
import com.example.CommunityHealthMedicalSystem.Exception.DuplicateResourceException;
import com.example.CommunityHealthMedicalSystem.Exception.IllegalArgumentException;
import com.example.CommunityHealthMedicalSystem.Exception.ResourceNotFound;
import com.example.CommunityHealthMedicalSystem.Model.Department;
import com.example.CommunityHealthMedicalSystem.Model.MedicalRecord;
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
    public MedicalStaff updateStaff(MedicalStaffDTO medicalStaffDTO) {

        // 1. find existing Medical Staff.
        MedicalStaff existingStaff = medicalStaffRepo.findById(medicalStaffDTO.getId())
                .orElseThrow(()-> new ResourceNotFound("Medical Staff not found."));

        // 2. validate input

        if (medicalStaffDTO == null){
            throw new IllegalArgumentException("Medical staff cannot be null.");
        }

        // 3. update fields from DTO

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

    public MedicalStaffDTO convertToDTO(MedicalStaff medicalStaff){
        MedicalStaffDTO dto = new MedicalStaffDTO();
        dto.setId(medicalStaff.getId());
        dto.setFirstName(medicalStaff.getFirstName());
        dto.setLastName(medicalStaff.getLastName());
        dto.setRole(medicalStaff.getRole());
        dto.setEmail(medicalStaff.getEmail());
        dto.setSpecialization(medicalStaff.getSpecialization());
        dto.setLicenseNumber(medicalStaff.getLicenseNumber());
        dto.setDepartmentId(medicalStaff.getDepartment().getId());

        return dto;
    }
}
