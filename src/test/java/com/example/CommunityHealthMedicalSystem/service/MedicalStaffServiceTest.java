package com.example.CommunityHealthMedicalSystem.service;

import com.example.CommunityHealthMedicalSystem.DTO.MedicalStaffDTO;
import com.example.CommunityHealthMedicalSystem.Exception.DuplicateResourceException;
import com.example.CommunityHealthMedicalSystem.Exception.IllegalArgumentException;
import com.example.CommunityHealthMedicalSystem.Exception.ResourceNotFound;
import com.example.CommunityHealthMedicalSystem.Model.Department;
import com.example.CommunityHealthMedicalSystem.Model.MedicalStaff;
import com.example.CommunityHealthMedicalSystem.Repository.DepartmentRepository;
import com.example.CommunityHealthMedicalSystem.Repository.MedicalStaffRepository;
import com.example.CommunityHealthMedicalSystem.Service.MedicalStaffServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

// 1. Setup the testing framework to use Mockito
@ExtendWith(MockitoExtension.class)
class MedicalStaffServiceImplTest {

    // 2. Mock dependencies
    @Mock
    private MedicalStaffRepository medicalStaffRepository;
    @Mock
    private DepartmentRepository departmentRepository;

    // 3. Inject mocks into the service class being tested
    @InjectMocks
    private MedicalStaffServiceImpl medicalStaffService;

    // Helper method for creating reusable objects
    private MedicalStaff createSampleStaff() {
        MedicalStaff staff = new MedicalStaff();
        staff.setId(10L);
        staff.setFirstName("Alice");
        staff.setLastName("Smith");
        staff.setEmail("alice.smith@hospital.com");
        staff.setLicenseNumber("LIC-456789");
        staff.setSpecialization("Cardiology");
        staff.setRole(MedicalStaff.Role.DOCTOR);
        staff.setDepartment(createSampleDepartment());
        return staff;
    }

    private MedicalStaffDTO createSampleStaffDTO() {
        MedicalStaffDTO dto = new MedicalStaffDTO();
        dto.setId(10L);
        dto.setFirstName("Alice");
        dto.setLastName("Smith");
        dto.setEmail("alice.smith@hospital.com");
        dto.setLicenseNumber("LIC-456789");
        dto.setSpecialization("Cardiology");
        dto.setRole(MedicalStaff.Role.DOCTOR);
        dto.setDepartmentId(1L);
        return dto;
    }

    private Department createSampleDepartment() {
        Department department = new Department();
        department.setId(1L);
        department.setName("Emergency");
        return department;
    }

    // --- Basic CRUD Operations Tests ---

    @Test
    void getAllMedicalStaff_ShouldReturnAllStaff() {
        // Given
        MedicalStaff staff1 = createSampleStaff();
        MedicalStaff staff2 = createSampleStaff();
        staff2.setId(11L);
        List<MedicalStaff> expectedStaff = Arrays.asList(staff1, staff2);
        when(medicalStaffRepository.findAll()).thenReturn(expectedStaff);

        // When
        List<MedicalStaff> result = medicalStaffService.getAllMedicalStaff();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(medicalStaffRepository, times(1)).findAll();
    }

    // --- Find By ID Test ---

    @Test
    void getMedicalStaffById_WithValidId_ShouldReturnStaff() {
        // Given
        Long staffId = 10L;
        MedicalStaff expectedStaff = createSampleStaff();
        when(medicalStaffRepository.findById(staffId)).thenReturn(Optional.of(expectedStaff));

        // When
        Optional<MedicalStaff> result = medicalStaffService.getMedicalStaffById(staffId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(expectedStaff, result.get());
        verify(medicalStaffRepository, times(1)).findById(staffId);
    }

    @Test
    void getMedicalStaffById_WithNullId_ShouldThrowException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> medicalStaffService.getMedicalStaffById(null));

        assertEquals("ID cannot be null", exception.getMessage());
        verify(medicalStaffRepository, never()).findById(any());
    }

    @Test
    void getMedicalStaffById_WithNonExistentId_ShouldReturnEmpty() {
        // Given
        Long staffId = 99L;
        when(medicalStaffRepository.findById(staffId)).thenReturn(Optional.empty());

        // When
        Optional<MedicalStaff> result = medicalStaffService.getMedicalStaffById(staffId);

        // Then
        assertFalse(result.isPresent());
    }

    // --- Create Staff Tests ---

    @Test
    void createStaff_WithValidData_ShouldReturnDTO() {
        // Given
        MedicalStaffDTO inputDTO = createSampleStaffDTO();
        inputDTO.setId(null);
        MedicalStaff savedStaff = createSampleStaff();

        when(medicalStaffRepository.findByEmailContainsIgnoreCase(anyString())).thenReturn(Optional.empty());
        when(medicalStaffRepository.findByLicenseNumber(anyString())).thenReturn(Optional.empty());
        when(departmentRepository.findById(anyLong())).thenReturn(Optional.of(createSampleDepartment()));
        when(medicalStaffRepository.save(any(MedicalStaff.class))).thenReturn(savedStaff);

        // When
        MedicalStaffDTO result = medicalStaffService.createStaff(inputDTO);

        // Then
        assertNotNull(result);
        assertEquals(savedStaff.getId(), result.getId());
        assertEquals(savedStaff.getEmail(), result.getEmail());
        verify(medicalStaffRepository, times(1)).save(any(MedicalStaff.class));
        verify(departmentRepository, times(1)).findById(inputDTO.getDepartmentId());
    }

    @Test
    void createStaff_WithNullDTO_ShouldThrowException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> medicalStaffService.createStaff(null));

        assertEquals("Medical Staff details are required.", exception.getMessage());
        verify(medicalStaffRepository, never()).save(any());
    }

    @Test
    void createStaff_WithExistingEmail_ShouldThrowDuplicateException() {
        // Given
        MedicalStaffDTO inputDTO = createSampleStaffDTO();
        when(medicalStaffRepository.findByEmailContainsIgnoreCase(inputDTO.getEmail()))
                .thenReturn(Optional.of(createSampleStaff()));

        // When & Then
        DuplicateResourceException exception = assertThrows(DuplicateResourceException.class,
                () -> medicalStaffService.createStaff(inputDTO));

        assertEquals("Error! This person already exits in database.", exception.getMessage());
        verify(medicalStaffRepository, never()).save(any());
    }

    @Test
    void createStaff_WithNullDepartmentId_ShouldThrowIllegalArgumentException() {
        // Given
        MedicalStaffDTO inputDTO = createSampleStaffDTO();
        inputDTO.setDepartmentId(null);
        when(medicalStaffRepository.findByEmailContainsIgnoreCase(anyString())).thenReturn(Optional.empty());
        when(medicalStaffRepository.findByLicenseNumber(anyString())).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> medicalStaffService.createStaff(inputDTO));

        assertEquals("Department ID is required", exception.getMessage());
        verify(medicalStaffRepository, never()).save(any());
        verify(departmentRepository, never()).findById(any());
    }

    @Test
    void createStaff_WithNonExistentDepartment_ShouldThrowResourceNotFound() {
        // Given
        MedicalStaffDTO inputDTO = createSampleStaffDTO();
        when(medicalStaffRepository.findByEmailContainsIgnoreCase(anyString())).thenReturn(Optional.empty());
        when(medicalStaffRepository.findByLicenseNumber(anyString())).thenReturn(Optional.empty());
        when(departmentRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFound exception = assertThrows(ResourceNotFound.class,
                () -> medicalStaffService.createStaff(inputDTO));

        assertTrue(exception.getMessage().startsWith("Department not found with id"));
        verify(medicalStaffRepository, never()).save(any());
    }

    // --- Update Staff Tests ---

    @Test
    void updateStaff_WithValidData_ShouldReturnUpdatedDTO() {
        // Given
        Long staffId = 10L;

        MedicalStaff existingStaff = createSampleStaff(); // License: "LIC-456789"

        MedicalStaffDTO updateDTO = createSampleStaffDTO();
        updateDTO.setId(staffId);
        updateDTO.setEmail("updated.alice.smith@hospital.com");
        updateDTO.setFirstName("UpdatedAlice");

        // Ensure the license number is explicitly different to trigger the check in the service
        updateDTO.setLicenseNumber("UPDATED-LIC-456789");

        // Stubbing
        when(medicalStaffRepository.findById(staffId)).thenReturn(Optional.of(existingStaff));
        when(medicalStaffRepository.existsByEmailAndIdNot(updateDTO.getEmail(), existingStaff.getId())).thenReturn(false);
        when(medicalStaffRepository.existsByLicenseNumberAndIdNot(updateDTO.getLicenseNumber(), existingStaff.getId())).thenReturn(false);
        when(medicalStaffRepository.save(any(MedicalStaff.class))).thenReturn(existingStaff);

        // When
        MedicalStaffDTO result = medicalStaffService.updateStaff(staffId, updateDTO);

        // Then
        assertNotNull(result);
        assertEquals("UpdatedAlice", result.getFirstName());
        assertEquals("updated.alice.smith@hospital.com", result.getEmail());

        // Verify all interactions happened
        verify(medicalStaffRepository, times(1)).findById(staffId);
        verify(medicalStaffRepository, times(1)).existsByEmailAndIdNot(updateDTO.getEmail(), existingStaff.getId());
        verify(medicalStaffRepository, times(1)).existsByLicenseNumberAndIdNot(updateDTO.getLicenseNumber(), existingStaff.getId());
        verify(medicalStaffRepository, times(1)).save(existingStaff);
    }

    @Test
    void updateStaff_WithNullDTO_ShouldThrowException() {
        // Given
        Long staffId = 10L;
        // The service needs a staff member before the DTO null check.
        when(medicalStaffRepository.findById(staffId)).thenReturn(Optional.of(createSampleStaff()));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> medicalStaffService.updateStaff(staffId, null));

        assertEquals("Medical staff cannot be null.", exception.getMessage());
        verify(medicalStaffRepository, times(1)).findById(staffId);
        verify(medicalStaffRepository, never()).save(any());
    }

    @Test
    void updateStaff_WithNonExistentId_ShouldThrowException() {
        // Given
        Long staffId = 99L;
        MedicalStaffDTO updateDTO = createSampleStaffDTO();
        when(medicalStaffRepository.findById(staffId)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFound exception = assertThrows(ResourceNotFound.class,
                () -> medicalStaffService.updateStaff(staffId, updateDTO));

        assertEquals("Medical Staff not found.", exception.getMessage());
        verify(medicalStaffRepository, times(1)).findById(staffId);
        verify(medicalStaffRepository, never()).save(any());
    }

    @Test
    void updateStaff_WithDuplicateEmail_ShouldThrowException() {
        // Given
        Long staffId = 10L;
        MedicalStaffDTO updateDTO = createSampleStaffDTO();
        updateDTO.setEmail("duplicate@hospital.com");

        MedicalStaff existingStaff = createSampleStaff();
        // Since the email is being changed, the service calls the email check
        when(medicalStaffRepository.findById(staffId)).thenReturn(Optional.of(existingStaff));
        when(medicalStaffRepository.existsByEmailAndIdNot(updateDTO.getEmail(), existingStaff.getId())).thenReturn(true);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> medicalStaffService.updateStaff(staffId, updateDTO));

        assertEquals("Email is already taken by another staff member.", exception.getMessage());
        verify(medicalStaffRepository, times(1)).existsByEmailAndIdNot(anyString(), anyLong());
        verify(medicalStaffRepository, never()).save(any());
    }

    @Test
    void updateStaff_WithDuplicateLicenseNumber_ShouldThrowException() {
        // Given
        Long staffId = 10L;
        MedicalStaffDTO updateDTO = createSampleStaffDTO();
        updateDTO.setLicenseNumber("DUP-LIC-999");

        MedicalStaff existingStaff = createSampleStaff();
        when(medicalStaffRepository.findById(staffId)).thenReturn(Optional.of(existingStaff));

        // DTO's default email is "alice.smith@hospital.com", same as existingStaff.
        // The service's email equality check will be false, so the email repository call is skipped.
        // **The email stubbing is removed to fix the UnnecessaryStubbingException.**

        // License number check fails (Stub at line ~321)
        when(medicalStaffRepository.existsByLicenseNumberAndIdNot(updateDTO.getLicenseNumber(), existingStaff.getId())).thenReturn(true);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> medicalStaffService.updateStaff(staffId, updateDTO));

        assertEquals("License number is in use by another staff member.", exception.getMessage());

        // Verify that the email check was correctly skipped (never called)
        verify(medicalStaffRepository, never()).existsByEmailAndIdNot(anyString(), anyLong());
        // Verify that the required license check was called
        verify(medicalStaffRepository, times(1)).existsByLicenseNumberAndIdNot(anyString(), anyLong());
        verify(medicalStaffRepository, never()).save(any());
    }

    // --- Delete Staff Tests ---

    @Test
    void deleteStaff_WithValidId_ShouldDeleteStaff() {
        // Given
        Long staffId = 10L;
        MedicalStaff staffToDelete = createSampleStaff();
        when(medicalStaffRepository.findById(staffId)).thenReturn(Optional.of(staffToDelete));

        // When
        medicalStaffService.deleteStaff(staffId);

        // Then
        verify(medicalStaffRepository, times(1)).findById(staffId);
        verify(medicalStaffRepository, times(1)).delete(staffToDelete);
    }

    @Test
    void deleteStaff_WithNullId_ShouldThrowException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> medicalStaffService.deleteStaff(null));

        assertEquals("ID cannot be a null field.", exception.getMessage());
        verify(medicalStaffRepository, never()).findById(any());
        verify(medicalStaffRepository, never()).delete(any());
    }

    @Test
    void deleteStaff_WithNonExistentId_ShouldThrowException() {
        // Given
        Long staffId = 99L;
        when(medicalStaffRepository.findById(staffId)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFound exception = assertThrows(ResourceNotFound.class,
                () -> medicalStaffService.deleteStaff(staffId));

        assertEquals("Medical Staff not found.", exception.getMessage());
        verify(medicalStaffRepository, times(1)).findById(staffId);
        verify(medicalStaffRepository, never()).delete(any());
    }

    // --- Search/Find Methods Tests (Examples) ---

    @Test
    void getByFirstNameIgnoreCase_WithValidName_ShouldReturnStaffList() {
        // Given
        String firstName = "Alice";
        List<MedicalStaff> expectedStaff = Arrays.asList(createSampleStaff());
        when(medicalStaffRepository.findByFirstNameContainsIgnoreCase(firstName)).thenReturn(expectedStaff);

        // When
        List<MedicalStaff> result = medicalStaffService.getByFirstNameIgnoreCase(firstName);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(medicalStaffRepository, times(1)).findByFirstNameContainsIgnoreCase(firstName);
    }

    @Test
    void getByRoleIgnoreCase_WithNullRole_ShouldThrowException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> medicalStaffService.getByRoleIgnoreCase(null));

        assertEquals("Role is a required field, it cannot be null.", exception.getMessage());
        verify(medicalStaffRepository, never()).findByRoleContains(any());
    }

    @Test
    void getByDepartment_WithValidId_ShouldReturnStaffList() {
        // Given
        Long departmentId = 1L;
        List<MedicalStaff> expectedStaff = Arrays.asList(createSampleStaff());
        when(medicalStaffRepository.findByDepartmentId(departmentId)).thenReturn(expectedStaff);

        // When
        List<MedicalStaff> result = medicalStaffService.getByDepartment(departmentId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(medicalStaffRepository, times(1)).findByDepartmentId(departmentId);
    }

    @Test
    void getByDepartment_WithNullId_ShouldThrowException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> medicalStaffService.getByDepartment(null));

        assertEquals("Department ID cannot be null", exception.getMessage());
        verify(medicalStaffRepository, never()).findByDepartmentId(any());
    }

    // --- Converter Test ---

    @Test
    void convertToDTO_WithValidStaffAndDepartment_ShouldReturnCorrectDTO() {
        // Given
        MedicalStaff staff = createSampleStaff();

        // When
        MedicalStaffDTO result = medicalStaffService.convertToDTO(staff);

        // Then
        assertNotNull(result);
        assertEquals(staff.getId(), result.getId());
        assertEquals(staff.getDepartment().getId(), result.getDepartmentId());
    }
}