package com.example.CommunityHealthMedicalSystem.Service;

import com.example.CommunityHealthMedicalSystem.DTO.MedicalStaffDTO;
import com.example.CommunityHealthMedicalSystem.Exception.DuplicateResourceException;
import com.example.CommunityHealthMedicalSystem.Exception.IllegalArgumentException;
import com.example.CommunityHealthMedicalSystem.Exception.ResourceNotFound;
import com.example.CommunityHealthMedicalSystem.Model.Department;
import com.example.CommunityHealthMedicalSystem.Model.MedicalRecord;
import com.example.CommunityHealthMedicalSystem.Model.MedicalStaff;
import com.example.CommunityHealthMedicalSystem.Repository.DepartmentRepository;
import com.example.CommunityHealthMedicalSystem.Repository.MedicalStaffRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MedicalStaffServiceImpl implements MedicalStaffService {

    private final MedicalStaffRepository medicalStaffRepo;
    private final DepartmentRepository departmentRepo;
    MedicalStaffServiceImpl(MedicalStaffRepository medicalStaffRepo, DepartmentRepository departmentRepo) {
        this.medicalStaffRepo = medicalStaffRepo;
        this.departmentRepo=departmentRepo;
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
    public MedicalStaffDTO createStaff(MedicalStaffDTO medicalStaffDTO) {
        if (medicalStaffDTO == null) {
            throw new IllegalArgumentException("Medical Staff details are requierd.");
        }


        if (medicalStaffDTO.getEmail() != null && medicalStaffRepo.findByEmailContainsIgnoreCase(
                medicalStaffDTO.getEmail()).isPresent()) {
            throw new DuplicateResourceException("Error! This person already exits in database.");
        }
        if (medicalStaffDTO.getLicenseNumber() != null && medicalStaffRepo.findByLicenseNumber(
                medicalStaffDTO.getLicenseNumber()).isPresent()) {
            throw new DuplicateResourceException("A staff member with this License Number already" +
                    "exists in database.");
        }

        // convert DTO to entity
        MedicalStaff medicalStaff = new MedicalStaff();
        medicalStaff.setLicenseNumber(medicalStaffDTO.getLicenseNumber());
        medicalStaff.setEmail(medicalStaffDTO.getEmail());
        medicalStaff.setRole(medicalStaffDTO.getRole());
        medicalStaff.setLastName(medicalStaffDTO.getLastName());
        medicalStaff.setFirstName(medicalStaffDTO.getFirstName());
        medicalStaff.setSpecialization(medicalStaffDTO.getSpecialization());

        // handle department
        if (medicalStaffDTO.getDepartmentId() != null){
            Department department = departmentRepo.findById(medicalStaffDTO.getDepartmentId())
                    .orElseThrow(()-> new ResourceNotFound("Department not found with id + " +
                            medicalStaffDTO.getDepartmentId()));
            medicalStaff.setDepartment(department);
        } else {
            throw new IllegalArgumentException("Department ID is required");
        }

        MedicalStaff savedStaff = medicalStaffRepo.save(medicalStaff);
        return convertToDTO(savedStaff);
    }

    @Override
    public MedicalStaffDTO updateStaff(MedicalStaffDTO medicalStaffDTO) {

        // 1. find existing Medical Staff.
        MedicalStaff existingStaff = medicalStaffRepo.findById(medicalStaffDTO.getId())
                .orElseThrow(() -> new ResourceNotFound("Medical Staff not found."));

        // 2. validate input

        if (medicalStaffDTO == null) {
            throw new IllegalArgumentException("Medical staff cannot be null.");
        }

        //3. check if email exists
        if (!existingStaff.getEmail().equals(medicalStaffDTO.getEmail())) {
            boolean emailExists = medicalStaffRepo.existsByEmailAndIdNot(medicalStaffDTO.getEmail(), existingStaff.getId());
            if (emailExists) {
                throw new IllegalArgumentException("Email is already taken by another staff member.");
            }
        }

        //4. check if License number is taken or used by another staff member.
        if (!existingStaff.getLicenseNumber().equals(medicalStaffDTO.getLicenseNumber())) {
            boolean licenseNumberExists = medicalStaffRepo.existsByLicenseNumberAndIdNot(medicalStaffDTO.getLicenseNumber(), existingStaff.getId());
            if (licenseNumberExists) {
                throw new IllegalArgumentException("License number is in use by another staff member.");
            }
        }

    // update only allowed fields.
        existingStaff.setEmail(medicalStaffDTO.getEmail());
        existingStaff.setFirstName(medicalStaffDTO.getFirstName());
        existingStaff.setLastName(medicalStaffDTO.getLastName());
        existingStaff.setRole(medicalStaffDTO.getRole());
        existingStaff.setLicenseNumber(medicalStaffDTO.getLicenseNumber());


    //save entity
    MedicalStaff updatedStaff = medicalStaffRepo.save(existingStaff);
        return

    convertToDTO(updatedStaff);
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
