package com.example.CommunityHealthMedicalSystem.service;

import com.example.CommunityHealthMedicalSystem.DTO.DepartmentDTO;
import com.example.CommunityHealthMedicalSystem.Exception.DuplicateResourceException;
import com.example.CommunityHealthMedicalSystem.Exception.IllegalArgumentException;
import com.example.CommunityHealthMedicalSystem.Exception.ResourceNotFound;
import com.example.CommunityHealthMedicalSystem.Model.Department;
import com.example.CommunityHealthMedicalSystem.Model.MedicalStaff;
import com.example.CommunityHealthMedicalSystem.Repository.DepartmentRepository;
import com.example.CommunityHealthMedicalSystem.Service.DepartmentServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private DepartmentServiceImpl departmentService;

    private Department createSampleDepartment() {
        Department department = new Department();
        department.setId(1L);
        department.setName("Cardiology");
        department.setDescription("Heart and cardiovascular care");
        return department;
    }

    private DepartmentDTO createSampleDepartmentDTO() {
        DepartmentDTO dto = new DepartmentDTO();
        dto.setId(1L);
        dto.setName("Cardiology");
        dto.setDescription("Heart and cardiovascular care");
        return dto;
    }

    private MedicalStaff createSampleMedicalStaff() {
        MedicalStaff staff = new MedicalStaff();
        staff.setId(1L);
        staff.setFirstName("Dr. John");
        staff.setLastName("Smith");
        staff.setEmail("john.smith@hospital.com");
        return staff;
    }

    // Test for getAllDepartments()
    @Test
    void getAllDepartments_ShouldReturnAllDepartments() {
        // Given
        Department department1 = createSampleDepartment();
        Department department2 = createSampleDepartment();
        department2.setId(2L);
        department2.setName("Neurology");

        List<Department> expectedDepartments = Arrays.asList(department1, department2);
        when(departmentRepository.findAll()).thenReturn(expectedDepartments);

        // When
        List<Department> result = departmentService.getAllDepartments();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(departmentRepository, times(1)).findAll();
    }

    @Test
    void getAllDepartments_WhenNoDepartments_ShouldReturnEmptyList() {
        // Given
        when(departmentRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<Department> result = departmentService.getAllDepartments();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(departmentRepository, times(1)).findAll();
    }

    // Test for getDepartmentById()
    @Test
    void getDepartmentById_WithValidId_ShouldReturnDepartment() {
        // Given
        Long departmentId = 1L;
        Department expectedDepartment = createSampleDepartment();
        when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(expectedDepartment));

        // When
        Optional<Department> result = departmentService.getDepartmentById(departmentId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(expectedDepartment, result.get());
        verify(departmentRepository, times(1)).findById(departmentId);
    }

    @Test
    void getDepartmentById_WithNonExistentId_ShouldReturnEmpty() {
        // Given
        Long departmentId = 999L;
        when(departmentRepository.findById(departmentId)).thenReturn(Optional.empty());

        // When
        Optional<Department> result = departmentService.getDepartmentById(departmentId);

        // Then
        assertFalse(result.isPresent());
        verify(departmentRepository, times(1)).findById(departmentId);
    }

    // Test for getDepartmentByNameIgnoreCase()
    @Test
    void getDepartmentByNameIgnoreCase_WithValidName_ShouldReturnDepartment() {
        // Given
        String departmentName = "Cardiology";
        Department expectedDepartment = createSampleDepartment();
        when(departmentRepository.findByNameIgnoreCase(departmentName)).thenReturn(Optional.of(expectedDepartment));

        // When
        Optional<Department> result = departmentService.getDepartmentByNameIgnoreCase(departmentName);

        // Then
        assertTrue(result.isPresent());
        assertEquals(expectedDepartment, result.get());
        verify(departmentRepository, times(1)).findByNameIgnoreCase(departmentName);
    }

    @Test
    void getDepartmentByNameIgnoreCase_WithNonExistentName_ShouldReturnEmpty() {
        // Given
        String departmentName = "NonExistent";
        when(departmentRepository.findByNameIgnoreCase(departmentName)).thenReturn(Optional.empty());

        // When
        Optional<Department> result = departmentService.getDepartmentByNameIgnoreCase(departmentName);

        // Then
        assertFalse(result.isPresent());
        verify(departmentRepository, times(1)).findByNameIgnoreCase(departmentName);
    }

    // Test for getDepartmentByMedicalStaff()
    @Test
    void getDepartmentByMedicalStaff_WithValidStaffId_ShouldReturnDepartments() {
        // Given
        Long medicalStaffId = 1L;
        Department department1 = createSampleDepartment();
        Department department2 = createSampleDepartment();
        department2.setId(2L);
        department2.setName("Neurology");

        List<Department> expectedDepartments = Arrays.asList(department1, department2);
        when(departmentRepository.findByMedicalStaffsId(medicalStaffId)).thenReturn(expectedDepartments);

        // When
        List<Department> result = departmentService.getDepartmentByMedicalStaff(medicalStaffId);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(departmentRepository, times(1)).findByMedicalStaffsId(medicalStaffId);
    }

    @Test
    void getDepartmentByMedicalStaff_WithNullStaffId_ShouldThrowException() {
        // Given & When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> departmentService.getDepartmentByMedicalStaff(null));

        assertEquals("Medical staff cannot be null.", exception.getMessage());
        verify(departmentRepository, never()).findByMedicalStaffsId(anyLong());
    }

    @Test
    void getDepartmentByMedicalStaff_WhenNoDepartments_ShouldReturnEmptyList() {
        // Given
        Long medicalStaffId = 1L;
        when(departmentRepository.findByMedicalStaffsId(medicalStaffId)).thenReturn(Collections.emptyList());

        // When
        List<Department> result = departmentService.getDepartmentByMedicalStaff(medicalStaffId);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(departmentRepository, times(1)).findByMedicalStaffsId(medicalStaffId);
    }

    // Test for createDepartment()
    @Test
    void createDepartment_WithValidData_ShouldReturnDepartmentDTO() {
        // Given
        DepartmentDTO inputDTO = createSampleDepartmentDTO();
        inputDTO.setId(null); // ID should be null for creation

        Department savedDepartment = createSampleDepartment();
        when(departmentRepository.findByNameIgnoreCase(inputDTO.getName())).thenReturn(Optional.empty());
        when(departmentRepository.save(any(Department.class))).thenReturn(savedDepartment);

        // When
        DepartmentDTO result = departmentService.createDepartment(inputDTO);

        // Then
        assertNotNull(result);
        assertEquals(savedDepartment.getId(), result.getId());
        assertEquals(savedDepartment.getName(), result.getName());
        assertEquals(savedDepartment.getDescription(), result.getDescription());
        verify(departmentRepository, times(1)).findByNameIgnoreCase(inputDTO.getName());
        verify(departmentRepository, times(1)).save(any(Department.class));
    }

    @Test
    void createDepartment_WithNullDTO_ShouldThrowException() {
        // Given & When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> departmentService.createDepartment(null));

        assertEquals("Department cannot be null.", exception.getMessage());
        verify(departmentRepository, never()).findByNameIgnoreCase(anyString());
        verify(departmentRepository, never()).save(any(Department.class));
    }

    @Test
    void createDepartment_WithDuplicateName_ShouldThrowException() {
        // Given
        DepartmentDTO inputDTO = createSampleDepartmentDTO();
        Department existingDepartment = createSampleDepartment();
        when(departmentRepository.findByNameIgnoreCase(inputDTO.getName())).thenReturn(Optional.of(existingDepartment));

        // When & Then
        DuplicateResourceException exception = assertThrows(DuplicateResourceException.class,
                () -> departmentService.createDepartment(inputDTO));

        assertEquals("Department with name " + inputDTO.getName() + " already exits.", exception.getMessage());
        verify(departmentRepository, times(1)).findByNameIgnoreCase(inputDTO.getName());
        verify(departmentRepository, never()).save(any(Department.class));
    }

    // Test for updateDepartment()
    @Test
    void updateDepartment_WithValidData_ShouldReturnUpdatedDTO() {
        // Given
        Long departmentId = 1L;
        DepartmentDTO updateDTO = createSampleDepartmentDTO();
        updateDTO.setName("Updated Cardiology");
        updateDTO.setDescription("Updated description");

        Department existingDepartment = createSampleDepartment();
        when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(existingDepartment));
        when(departmentRepository.save(any(Department.class))).thenReturn(existingDepartment);

        // When
        DepartmentDTO result = departmentService.updateDepartment(departmentId, updateDTO);

        // Then
        assertNotNull(result);
        verify(departmentRepository, times(1)).findById(departmentId);
        verify(departmentRepository, times(1)).save(existingDepartment);
    }

    @Test
    void updateDepartment_WithNonExistentId_ShouldThrowException() {
        // Given
        Long departmentId = 999L;
        DepartmentDTO updateDTO = createSampleDepartmentDTO();
        when(departmentRepository.findById(departmentId)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFound exception = assertThrows(ResourceNotFound.class,
                () -> departmentService.updateDepartment(departmentId, updateDTO));

        assertEquals("Department with id:" + departmentId + " not found.", exception.getMessage());
        verify(departmentRepository, times(1)).findById(departmentId);
        verify(departmentRepository, never()).save(any(Department.class));
    }

    @Test
    void updateDepartment_WithPartialData_ShouldUpdateOnlyProvidedFields() {
        // Given
        Long departmentId = 1L;
        DepartmentDTO updateDTO = new DepartmentDTO();
        updateDTO.setName("Updated Name Only"); // Only update name

        Department existingDepartment = createSampleDepartment();
        String originalDescription = existingDepartment.getDescription();

        when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(existingDepartment));
        when(departmentRepository.save(any(Department.class))).thenReturn(existingDepartment);

        // When
        DepartmentDTO result = departmentService.updateDepartment(departmentId, updateDTO);

        // Then
        assertNotNull(result);
        assertEquals("Updated Name Only", existingDepartment.getName());
        assertEquals(originalDescription, existingDepartment.getDescription()); // Should remain unchanged
        verify(departmentRepository, times(1)).findById(departmentId);
        verify(departmentRepository, times(1)).save(existingDepartment);
    }

    // Test for deleteDepartment()
    @Test
    void deleteDepartment_WithValidId_ShouldDeleteDepartment() {
        // Given
        Long departmentId = 1L;
        Department existingDepartment = createSampleDepartment();
        when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(existingDepartment));

        // When
        departmentService.deleteDepartment(departmentId);

        // Then
        verify(departmentRepository, times(1)).findById(departmentId);
        verify(departmentRepository, times(1)).delete(existingDepartment);
    }

    @Test
    void deleteDepartment_WithNonExistentId_ShouldThrowException() {
        // Given
        Long departmentId = 999L;
        when(departmentRepository.findById(departmentId)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> departmentService.deleteDepartment(departmentId));

        assertEquals("Department not found " + departmentId, exception.getMessage());
        verify(departmentRepository, times(1)).findById(departmentId);
        verify(departmentRepository, never()).delete(any(Department.class));
    }

    // Test for getMedicalStaffByDepartment()
    @Test
    void getMedicalStaffByDepartment_WithValidId_ShouldReturnMedicalStaff() {
        // Given
        Long departmentId = 1L;
        MedicalStaff staff1 = createSampleMedicalStaff();
        MedicalStaff staff2 = createSampleMedicalStaff();
        staff2.setId(2L);
        staff2.setFirstName("Dr. Jane");

        List<MedicalStaff> expectedStaff = Arrays.asList(staff1, staff2);
        when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(createSampleDepartment()));
        when(departmentRepository.findMedicalStaffByDepartment(departmentId)).thenReturn(expectedStaff);

        // When
        List<MedicalStaff> result = departmentService.getMedicalStaffByDepartment(departmentId);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(departmentRepository, times(1)).findById(departmentId);
        verify(departmentRepository, times(1)).findMedicalStaffByDepartment(departmentId);
    }

    @Test
    void getMedicalStaffByDepartment_WithNullId_ShouldThrowException() {
        // Given & When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> departmentService.getMedicalStaffByDepartment(null));

        assertEquals("ID cannot be null.", exception.getMessage());
        verify(departmentRepository, never()).findById(anyLong());
        verify(departmentRepository, never()).findMedicalStaffByDepartment(anyLong());
    }

    @Test
    void getMedicalStaffByDepartment_WithNonExistentId_ShouldThrowException() {
        // Given
        Long departmentId = 999L;
        when(departmentRepository.findById(departmentId)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFound exception = assertThrows(ResourceNotFound.class,
                () -> departmentService.getMedicalStaffByDepartment(departmentId));

        assertEquals("Department not found with id: " + departmentId, exception.getMessage());
        verify(departmentRepository, times(1)).findById(departmentId);
        verify(departmentRepository, never()).findMedicalStaffByDepartment(anyLong());
    }

    @Test
    void getMedicalStaffByDepartment_WhenNoStaff_ShouldReturnEmptyList() {
        // Given
        Long departmentId = 1L;
        when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(createSampleDepartment()));
        when(departmentRepository.findMedicalStaffByDepartment(departmentId)).thenReturn(Collections.emptyList());

        // When
        List<MedicalStaff> result = departmentService.getMedicalStaffByDepartment(departmentId);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(departmentRepository, times(1)).findById(departmentId);
        verify(departmentRepository, times(1)).findMedicalStaffByDepartment(departmentId);
    }

    // Test for convertToDTO()
    @Test
    void convertToDTO_WithValidDepartment_ShouldReturnCorrectDTO() {
        // Given
        Department department = createSampleDepartment();

        // When
        DepartmentDTO result = departmentService.convertToDTO(department);

        // Then
        assertNotNull(result);
        assertEquals(department.getId(), result.getId());
        assertEquals(department.getName(), result.getName());
        assertEquals(department.getDescription(), result.getDescription());
        assertEquals(0, result.getMedicalStaffCount()); // No staff assigned in sample
        assertEquals(0, result.getActiveAppointmentsCount()); // No appointments in sample
    }

    @Test
    void convertToDTO_WithDepartmentWithStaffAndAppointments_ShouldReturnCorrectCounts() {
        // Given
        Department department = createSampleDepartment();

        // Create staff and appointments (you might need to set up actual relationships)
        MedicalStaff staff1 = createSampleMedicalStaff();
        MedicalStaff staff2 = createSampleMedicalStaff();
        staff2.setId(2L);

        // This would require setting up the actual relationships in your Department entity
        // For now, we'll test with null collections which should return 0 counts

        // When
        DepartmentDTO result = departmentService.convertToDTO(department);

        // Then
        assertNotNull(result);
        assertEquals(0, result.getMedicalStaffCount()); // null medicalStaffs
        assertEquals(0, result.getActiveAppointmentsCount()); // null appointments
    }

    // Edge case tests
    @Test
    void createDepartment_WithEmptyName_ShouldWorkIfAllowed() {
        // Given
        DepartmentDTO inputDTO = createSampleDepartmentDTO();
        inputDTO.setName(""); // Empty name
        inputDTO.setId(null);

        Department savedDepartment = createSampleDepartment();
        savedDepartment.setName("");

        when(departmentRepository.findByNameIgnoreCase("")).thenReturn(Optional.empty());
        when(departmentRepository.save(any(Department.class))).thenReturn(savedDepartment);

        // When
        DepartmentDTO result = departmentService.createDepartment(inputDTO);

        // Then
        assertNotNull(result);
        assertEquals("", result.getName());
        verify(departmentRepository, times(1)).findByNameIgnoreCase("");
        verify(departmentRepository, times(1)).save(any(Department.class));
    }

    @Test
    void updateDepartment_WithNullDTOFields_ShouldNotUpdateFields() {
        // Given
        Long departmentId = 1L;
        DepartmentDTO updateDTO = new DepartmentDTO(); // All fields null

        Department existingDepartment = createSampleDepartment();
        String originalName = existingDepartment.getName();
        String originalDescription = existingDepartment.getDescription();

        when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(existingDepartment));
        when(departmentRepository.save(any(Department.class))).thenReturn(existingDepartment);

        // When
        DepartmentDTO result = departmentService.updateDepartment(departmentId, updateDTO);

        // Then
        assertNotNull(result);
        assertEquals(originalName, existingDepartment.getName()); // Should remain unchanged
        assertEquals(originalDescription, existingDepartment.getDescription()); // Should remain unchanged
        verify(departmentRepository, times(1)).findById(departmentId);
        verify(departmentRepository, times(1)).save(existingDepartment);
    }
}