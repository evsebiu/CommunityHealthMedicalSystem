package com.example.CommunityHealthMedicalSystem.service;

import com.example.CommunityHealthMedicalSystem.DTO.PatientDTO;
import com.example.CommunityHealthMedicalSystem.Exception.IllegalArgumentException;
import com.example.CommunityHealthMedicalSystem.Exception.ResourceNotFound;
import com.example.CommunityHealthMedicalSystem.Model.Patient;
import com.example.CommunityHealthMedicalSystem.Repository.PatientRepository;
import com.example.CommunityHealthMedicalSystem.Service.PatientServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceImplTest {

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private PatientServiceImpl patientService;

    private Patient createSamplePatient() {
        Patient patient = new Patient();
        patient.setId(1L);
        patient.setFirstName("John");
        patient.setLastName("Doe");
        patient.setEmail("john.doe@example.com");
        patient.setPhoneNumber("123-456-7890");
        patient.setDateOfBirth(LocalDate.of(1990, 1, 1));
        patient.setAddress("123 Main St");
        patient.setNationalId("NID123456");
        return patient;
    }

    private PatientDTO createSamplePatientDTO() {
        PatientDTO dto = new PatientDTO();
        dto.setId(1L);
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setEmail("john.doe@example.com");
        dto.setPhoneNumber("123-456-7890");
        dto.setDateOfBirth(LocalDate.of(1990, 1, 1));
        dto.setAddress("123 Main St");
        dto.setNationalId("NID123456");
        return dto;
    }

    // Test for getAllPatients()
    @Test
    void getAllPatients_ShouldReturnAllPatients() {
        // Given
        Patient patient1 = createSamplePatient();
        Patient patient2 = createSamplePatient();
        patient2.setId(2L);
        patient2.setEmail("jane.doe@example.com");

        List<Patient> expectedPatients = Arrays.asList(patient1, patient2);
        when(patientRepository.findAll()).thenReturn(expectedPatients);

        // When
        List<Patient> result = patientService.getAllPatients();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(patientRepository, times(1)).findAll();
    }

    // Test for findById()
    @Test
    void findById_WithValidId_ShouldReturnPatient() {
        // Given
        Long patientId = 1L;
        Patient expectedPatient = createSamplePatient();
        when(patientRepository.findById(patientId)).thenReturn(Optional.of(expectedPatient));

        // When
        Optional<Patient> result = patientService.findById(patientId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(expectedPatient, result.get());
        verify(patientRepository, times(1)).findById(patientId);
    }

    @Test
    void findById_WithNullId_ShouldThrowException() {
        // Given & When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> patientService.findById(null));

        assertEquals("Patient ID cannot be null.", exception.getMessage());
        verify(patientRepository, never()).findById(any());
    }

    @Test
    void findById_WithNonExistentId_ShouldReturnEmpty() {
        // Given
        Long patientId = 999L;
        when(patientRepository.findById(patientId)).thenReturn(Optional.empty());

        // When
        Optional<Patient> result = patientService.findById(patientId);

        // Then
        assertFalse(result.isPresent());
        verify(patientRepository, times(1)).findById(patientId);
    }

    // Test for findByFirstName()
    @Test
    void findByFirstName_WithValidName_ShouldReturnPatients() {
        // Given
        String firstName = "John";
        List<Patient> expectedPatients = Arrays.asList(createSamplePatient());
        when(patientRepository.findByFirstNameContainingIgnoreCase(firstName)).thenReturn(expectedPatients);

        // When
        List<Patient> result = patientService.findByFirstName(firstName);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(patientRepository, times(1)).findByFirstNameContainingIgnoreCase(firstName);
    }

    @Test
    void findByFirstName_WithNullName_ShouldThrowException() {
        // Given & When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> patientService.findByFirstName(null));

        assertEquals("Patient first name is a required field.", exception.getMessage());
        verify(patientRepository, never()).findByFirstNameContainingIgnoreCase(anyString());
    }

    // Test for findByLastName()
    @Test
    void findByLastName_WithValidName_ShouldReturnPatients() {
        // Given
        String lastName = "Doe";
        List<Patient> expectedPatients = Arrays.asList(createSamplePatient());
        when(patientRepository.findByLastNameContainingIgnoreCase(lastName)).thenReturn(expectedPatients);

        // When
        List<Patient> result = patientService.findByLastName(lastName);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(patientRepository, times(1)).findByLastNameContainingIgnoreCase(lastName);
    }

    @Test
    void findByLastName_WithNullName_ShouldThrowException() {
        // Given & When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> patientService.findByLastName(null));

        assertEquals("Patient last name is required.", exception.getMessage());
        verify(patientRepository, never()).findByLastNameContainingIgnoreCase(anyString());
    }

    // Test for findByEmail()
    @Test
    void findByEmail_WithValidEmail_ShouldReturnPatient() {
        // Given
        String email = "john.doe@example.com";
        Patient expectedPatient = createSamplePatient();
        when(patientRepository.findByEmail(email)).thenReturn(Optional.of(expectedPatient));

        // When
        Optional<Patient> result = patientService.findByEmail(email);

        // Then
        assertTrue(result.isPresent());
        assertEquals(expectedPatient, result.get());
        verify(patientRepository, times(1)).findByEmail(email);
    }

    @Test
    void findByEmail_WithNullEmail_ShouldThrowException() {
        // Given & When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> patientService.findByEmail(null));

        assertEquals("Email is a required field. It cannot be null.", exception.getMessage());
        verify(patientRepository, never()).findByEmail(anyString());
    }

    // Test for createPatient()
    @Test
    void createPatient_WithValidData_ShouldReturnPatientDTO() {
        // Given
        PatientDTO inputDTO = createSamplePatientDTO();
        inputDTO.setId(null); // ID should be null for creation

        Patient savedPatient = createSamplePatient();
        when(patientRepository.save(any(Patient.class))).thenReturn(savedPatient);

        // When
        PatientDTO result = patientService.createPatient(inputDTO);

        // Then
        assertNotNull(result);
        assertEquals(savedPatient.getId(), result.getId());
        assertEquals(savedPatient.getFirstName(), result.getFirstName());
        assertEquals(savedPatient.getLastName(), result.getLastName());
        assertEquals(savedPatient.getEmail(), result.getEmail());
        verify(patientRepository, times(1)).save(any(Patient.class));
    }

    @Test
    void createPatient_WithNullDTO_ShouldThrowException() {
        // Given & When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> patientService.createPatient(null));

        assertEquals("Patient's details are required.", exception.getMessage());
        verify(patientRepository, never()).save(any(Patient.class));
    }

    // Test for deletePatient()
    @Test
    void deletePatient_WithValidId_ShouldDeletePatient() {
        // Given
        Long patientId = 1L;
        Patient existingPatient = createSamplePatient();
        when(patientRepository.findById(patientId)).thenReturn(Optional.of(existingPatient));

        // When
        patientService.deletePatient(patientId);

        // Then
        verify(patientRepository, times(1)).findById(patientId);
        verify(patientRepository, times(1)).delete(existingPatient);
    }

    @Test
    void deletePatient_WithNullId_ShouldThrowException() {
        // Given & When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> patientService.deletePatient(null));

        assertEquals("Patient ID is required.", exception.getMessage());
        verify(patientRepository, never()).findById(any());
        verify(patientRepository, never()).delete(any(Patient.class));
    }

    @Test
    void deletePatient_WithNonExistentId_ShouldThrowException() {
        // Given
        Long patientId = 999L;
        when(patientRepository.findById(patientId)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFound exception = assertThrows(ResourceNotFound.class,
                () -> patientService.deletePatient(patientId));

        assertEquals("Patient does not exists in database", exception.getMessage());
        verify(patientRepository, times(1)).findById(patientId);
        verify(patientRepository, never()).delete(any(Patient.class));
    }

    // Test for updatePatient()
    @Test
    void updatePatient_WithValidData_ShouldReturnUpdatedPatientDTO() {
        // Given
        Long patientId = 1L;
        PatientDTO updateDTO = createSamplePatientDTO();
        // **ADD CHANGES HERE to make the email/NationalId different**
        updateDTO.setFirstName("UpdatedJohn");
        updateDTO.setLastName("UpdatedDoe");
        updateDTO.setEmail("updated.john.doe@example.com"); // <-- ADD THIS
        updateDTO.setNationalId("UPDATED-NID123456"); // <-- ADD THIS

        Patient existingPatient = createSamplePatient();
        when(patientRepository.findById(patientId)).thenReturn(Optional.of(existingPatient));

        // The service method should now call these because the email/NationalId in the DTO changed.
        when(patientRepository.existsByEmailAndIdNot(updateDTO.getEmail(), patientId)).thenReturn(false);
        when(patientRepository.existsByNationalIdAndIdNot(updateDTO.getNationalId(), patientId)).thenReturn(false);

        when(patientRepository.save(any(Patient.class))).thenReturn(existingPatient);

        // When
        PatientDTO result = patientService.updatePatient(patientId, updateDTO);

        // Then
        assertNotNull(result);
        // You should also add assertions to check if the fields were actually updated in the resulting DTO
        assertEquals(updateDTO.getFirstName(), result.getFirstName());
        assertEquals(updateDTO.getEmail(), result.getEmail());
        // ...
        verify(patientRepository, times(1)).findById(patientId);
        verify(patientRepository, times(1)).save(existingPatient);

        // **ADD VERIFICATIONS for the existence checks to be thorough**
        verify(patientRepository, times(1)).existsByEmailAndIdNot(updateDTO.getEmail(), patientId);
        verify(patientRepository, times(1)).existsByNationalIdAndIdNot(updateDTO.getNationalId(), patientId);
    }

    @Test
    void updatePatient_WithNonExistentId_ShouldThrowException() {
        // Given
        Long patientId = 999L;
        PatientDTO updateDTO = createSamplePatientDTO();
        when(patientRepository.findById(patientId)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFound exception = assertThrows(ResourceNotFound.class,
                () -> patientService.updatePatient(patientId, updateDTO));

        assertEquals("Patient not found.", exception.getMessage());
        verify(patientRepository, times(1)).findById(patientId);
        verify(patientRepository, never()).save(any(Patient.class));
    }

    @Test
    void updatePatient_WithDuplicateEmail_ShouldThrowException() {
        // Given
        Long patientId = 1L;
        PatientDTO updateDTO = createSamplePatientDTO();
        updateDTO.setEmail("new.email@example.com");

        Patient existingPatient = createSamplePatient();
        when(patientRepository.findById(patientId)).thenReturn(Optional.of(existingPatient));
        when(patientRepository.existsByEmailAndIdNot(updateDTO.getEmail(), patientId)).thenReturn(true);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> patientService.updatePatient(patientId, updateDTO));

        assertEquals("Email is already taken by another patient.", exception.getMessage());
        verify(patientRepository, never()).save(any(Patient.class));
    }

    @Test
    void updatePatient_WithNullDTO_ShouldThrowException() {
        // Given
        Long patientId = 1L;
        Patient existingPatient = createSamplePatient();
        when(patientRepository.findById(patientId)).thenReturn(Optional.of(existingPatient));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> patientService.updatePatient(patientId, null));

        assertEquals("Patient cannot be null.", exception.getMessage());
        verify(patientRepository, never()).save(any(Patient.class));
    }

    // Test for convertToDTO()
    @Test
    void convertToDTO_WithValidPatient_ShouldReturnCorrectDTO() {
        // Given
        Patient patient = createSamplePatient();

        // When
        PatientDTO result = patientService.convertToDTO(patient);

        // Then
        assertNotNull(result);
        assertEquals(patient.getId(), result.getId());
        assertEquals(patient.getFirstName(), result.getFirstName());
        assertEquals(patient.getLastName(), result.getLastName());
        assertEquals(patient.getEmail(), result.getEmail());
        assertEquals(patient.getPhoneNumber(), result.getPhoneNumber());
        assertEquals(patient.getDateOfBirth(), result.getDateOfBirth());
        assertEquals(patient.getAddress(), result.getAddress());
        assertEquals(patient.getNationalId(), result.getNationalId());
    }

    // Test for searchPatients()
    @Test
    void searchPatients_WithValidParameters_ShouldReturnPatients() {
        // Given
        String firstName = "John";
        String lastName = "Doe";
        String email = "john.doe@example.com";
        String address = "123 Main St";

        List<Patient> expectedPatients = Arrays.asList(createSamplePatient());
        when(patientRepository.searchPatients(firstName, lastName, email, address)).thenReturn(expectedPatients);

        // When
        List<Patient> result = patientService.searchPatients(firstName, lastName, email, address);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(patientRepository, times(1)).searchPatients(firstName, lastName, email, address);
    }

    // Additional edge case tests
    @Test
    void findByDateOfBirth_WithValidDate_ShouldReturnPatients() {
        // Given
        LocalDate dateOfBirth = LocalDate.of(1990, 1, 1);
        List<Patient> expectedPatients = Arrays.asList(createSamplePatient());
        when(patientRepository.findByDateOfBirth(dateOfBirth)).thenReturn(expectedPatients);

        // When
        List<Patient> result = patientService.findByDateOfBirth(dateOfBirth);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(patientRepository, times(1)).findByDateOfBirth(dateOfBirth);
    }

    @Test
    void findByDateOfBirth_WithNullDate_ShouldThrowException() {
        // Given & When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> patientService.findByDateOfBirth(null));

        assertEquals("Patient's date of birth is required.", exception.getMessage());
        verify(patientRepository, never()).findByDateOfBirth(any());
    }
}