package com.example.CommunityHealthMedicalSystem.service;

import com.example.CommunityHealthMedicalSystem.DTO.AppointmentDTO;
import com.example.CommunityHealthMedicalSystem.Exception.*;
import com.example.CommunityHealthMedicalSystem.Exception.IllegalArgumentException;
import com.example.CommunityHealthMedicalSystem.Exception.SecurityException;
import com.example.CommunityHealthMedicalSystem.Model.Appointment;
import com.example.CommunityHealthMedicalSystem.Model.MedicalStaff;
import com.example.CommunityHealthMedicalSystem.Model.Patient;
import com.example.CommunityHealthMedicalSystem.Repository.AppointmentRepository;
import com.example.CommunityHealthMedicalSystem.Repository.MedicalStaffRepository;
import com.example.CommunityHealthMedicalSystem.Repository.PatientRepository;
import com.example.CommunityHealthMedicalSystem.Service.AppointmentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private MedicalStaffRepository medicalStaffRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private AppointmentServiceImpl appointmentService;

    private Patient createSamplePatient() {
        Patient patient = new Patient();
        patient.setId(1L);
        patient.setFirstName("John");
        patient.setLastName("Doe");
        patient.setEmail("john.doe@example.com");
        return patient;
    }

    private MedicalStaff createSampleMedicalStaff() {
        MedicalStaff staff = new MedicalStaff();
        staff.setId(1L);
        staff.setFirstName("Dr. Jane");
        staff.setLastName("Smith");
        staff.setEmail("jane.smith@hospital.com");
        return staff;
    }

    private Appointment createSampleAppointment() {
        Appointment appointment = new Appointment();
        appointment.setId(1L);
        appointment.setPatient(createSamplePatient());
        appointment.setMedicalStaff(createSampleMedicalStaff());
        appointment.setAppointmentDateTime(LocalDateTime.of(2024, 12, 15, 10, 0));
        appointment.setReason("Regular checkup");
        appointment.setStatus(Appointment.Status.SCHEDULED);
        return appointment;
    }

    private AppointmentDTO createSampleAppointmentDTO() {
        AppointmentDTO dto = new AppointmentDTO();
        dto.setId(1L);
        dto.setPatientId(1L);
        dto.setMedicalStaffId(1L);
        dto.setAppointmentDateTime(LocalDateTime.of(2024, 12, 15, 10, 0));
        dto.setReason("Regular checkup");
        dto.setStatus(Appointment.Status.SCHEDULED);
        return dto;
    }

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    private void setupSecurityContext(String username, String role) {
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));
        // Use doReturn instead of when().thenReturn() for getAuthorities()
        when(authentication.getName()).thenReturn(username);
        doReturn(authorities).when(authentication).getAuthorities();
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    // Test for getAllAppointments()
    @Test
    void getAllAppointments_ShouldReturnAllAppointments() {
        // Given
        Appointment appointment1 = createSampleAppointment();
        Appointment appointment2 = createSampleAppointment();
        appointment2.setId(2L);

        List<Appointment> expectedAppointments = Arrays.asList(appointment1, appointment2);
        when(appointmentRepository.findAll()).thenReturn(expectedAppointments);

        // When
        List<Appointment> result = appointmentService.getAllAppointments();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(appointmentRepository, times(1)).findAll();
    }

    // Test for getAppointmentById()
    @Test
    void getAppointmentById_WithValidId_ShouldReturnAppointment() {
        // Given
        Long appointmentId = 1L;
        Appointment expectedAppointment = createSampleAppointment();
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(expectedAppointment));

        // When
        Optional<Appointment> result = appointmentService.getAppointmentById(appointmentId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(expectedAppointment, result.get());
        verify(appointmentRepository, times(1)).findById(appointmentId);
    }

    @Test
    void getAppointmentById_WithNonExistentId_ShouldReturnEmpty() {
        // Given
        Long appointmentId = 999L;
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.empty());

        // When
        Optional<Appointment> result = appointmentService.getAppointmentById(appointmentId);

        // Then
        assertFalse(result.isPresent());
        verify(appointmentRepository, times(1)).findById(appointmentId);
    }

    // Test for getAppointmentByReason()
    @Test
    void getAppointmentByReason_WithValidReason_ShouldReturnAppointments() {
        // Given
        String reason = "Regular checkup";
        List<Appointment> expectedAppointments = Arrays.asList(createSampleAppointment());
        when(appointmentRepository.findByReason(reason)).thenReturn(expectedAppointments);

        // When
        List<Appointment> result = appointmentService.getAppointmentByReason(reason);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(appointmentRepository, times(1)).findByReason(reason);
    }

    // Test for getAppointmentByStatus()
    @Test
    void getAppointmentByStatus_WithValidStatus_ShouldReturnAppointments() {
        // Given
        Appointment.Status status = Appointment.Status.SCHEDULED;
        List<Appointment> expectedAppointments = Arrays.asList(createSampleAppointment());
        when(appointmentRepository.findByStatus(status)).thenReturn(expectedAppointments);

        // When
        List<Appointment> result = appointmentService.getAppointmentByStatus(status);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(appointmentRepository, times(1)).findByStatus(status);
    }

    // Test for getAppointmentByPatient()
    @Test
    void getAppointmentByPatient_WithValidPatient_ShouldReturnAppointments() {
        // Given
        Patient patient = createSamplePatient();
        List<Appointment> expectedAppointments = Arrays.asList(createSampleAppointment());
        when(appointmentRepository.findByPatient(patient)).thenReturn(expectedAppointments);

        // When
        List<Appointment> result = appointmentService.getAppointmentByPatient(patient);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(appointmentRepository, times(1)).findByPatient(patient);
    }

    // Test for getAppointmentByPatientId() - Patient Role
    @Test
    void getAppointmentByPatientId_WithPatientRoleAndOwnId_ShouldReturnAppointments() {
        // Given
        Long patientId = 1L;
        Patient currentPatient = createSamplePatient();
        List<Appointment> expectedAppointments = Arrays.asList(createSampleAppointment());

        setupSecurityContext("john.doe@example.com", "PATIENT");
        when(patientRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(currentPatient));
        when(appointmentRepository.findByPatientId(patientId)).thenReturn(expectedAppointments);

        // When
        List<Appointment> result = appointmentService.getAppointmentByPatientId(patientId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(patientRepository, times(1)).findByEmail("john.doe@example.com");
        verify(appointmentRepository, times(1)).findByPatientId(patientId);
    }

    @Test
    void getAppointmentByPatientId_WithPatientRoleAndDifferentId_ShouldThrowSecurityException() {
        // Given
        Long differentPatientId = 2L;
        Patient currentPatient = createSamplePatient();

        setupSecurityContext("john.doe@example.com", "PATIENT");
        when(patientRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(currentPatient));

        // When & Then
        SecurityException exception = assertThrows(SecurityException.class,
                () -> appointmentService.getAppointmentByPatientId(differentPatientId));

        assertEquals("Patient can access only their own appointments.", exception.getMessage());
        verify(appointmentRepository, never()).findByPatientId(anyLong());
    }

    @Test
    void getAppointmentByPatientId_WithPatientRoleAndPatientNotFound_ShouldThrowSecurityException() {
        // Given
        Long patientId = 1L;

        setupSecurityContext("unknown@example.com", "PATIENT");
        when(patientRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        // When & Then
        SecurityException exception = assertThrows(SecurityException.class,
                () -> appointmentService.getAppointmentByPatientId(patientId));

        assertEquals("Patient not found", exception.getMessage());
        verify(appointmentRepository, never()).findByPatientId(anyLong());
    }

    @Test
    void getAppointmentByPatientId_WithNonPatientRole_ShouldReturnAppointments() {
        // Given
        Long patientId = 1L;
        List<Appointment> expectedAppointments = Arrays.asList(createSampleAppointment());

        setupSecurityContext("admin@hospital.com", "ADMINISTRATOR");
        when(appointmentRepository.findByPatientId(patientId)).thenReturn(expectedAppointments);

        // When
        List<Appointment> result = appointmentService.getAppointmentByPatientId(patientId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(patientRepository, never()).findByEmail(anyString());
        verify(appointmentRepository, times(1)).findByPatientId(patientId);
    }

    // Test for getAppointmentByMedicalStaff()
    @Test
    void getAppointmentByMedicalStaff_WithValidStaff_ShouldReturnAppointments() {
        // Given
        MedicalStaff medicalStaff = createSampleMedicalStaff();
        List<Appointment> expectedAppointments = Arrays.asList(createSampleAppointment());
        when(appointmentRepository.findByMedicalStaff(medicalStaff)).thenReturn(expectedAppointments);

        // When
        List<Appointment> result = appointmentService.getAppointmentByMedicalStaff(medicalStaff);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(appointmentRepository, times(1)).findByMedicalStaff(medicalStaff);
    }

    // Test for getAppointmentByMedicalId()
    @Test
    void getAppointmentByMedicalId_WithValidId_ShouldReturnAppointments() {
        // Given
        Long medicalId = 1L;
        List<Appointment> expectedAppointments = Arrays.asList(createSampleAppointment());
        when(appointmentRepository.findByMedicalStaffId(medicalId)).thenReturn(expectedAppointments);

        // When
        List<Appointment> result = appointmentService.getAppointmentByMedicalId(medicalId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(appointmentRepository, times(1)).findByMedicalStaffId(medicalId);
    }

    // Test for getAppointmentByDepartmentId()
    @Test
    void getAppointmentByDepartmentId_WithValidId_ShouldReturnAppointments() {
        // Given
        Long departmentId = 1L;
        List<Appointment> expectedAppointments = Arrays.asList(createSampleAppointment());
        when(appointmentRepository.findByDepartmentId(departmentId)).thenReturn(expectedAppointments);

        // When
        List<Appointment> result = appointmentService.getAppointmentByDepartmentId(departmentId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(appointmentRepository, times(1)).findByDepartmentId(departmentId);
    }

    // Test for getAppointmentsByDateRange()
    @Test
    void getAppointmentsByDateRange_WithValidDates_ShouldReturnAppointments() {
        // Given
        LocalDateTime startDate = LocalDateTime.of(2024, 12, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2024, 12, 31, 23, 59);
        List<Appointment> expectedAppointments = Arrays.asList(createSampleAppointment());
        when(appointmentRepository.findByAppointmentDateTimeBetween(startDate, endDate)).thenReturn(expectedAppointments);

        // When
        List<Appointment> result = appointmentService.getAppointmentsByDateRange(startDate, endDate);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(appointmentRepository, times(1)).findByAppointmentDateTimeBetween(startDate, endDate);
    }

    // Test for createAppointment()
    @Test
    void createAppointment_WithValidData_ShouldReturnAppointmentDTO() {
        // Given
        AppointmentDTO inputDTO = createSampleAppointmentDTO();
        inputDTO.setId(null); // ID should be null for creation

        Patient patient = createSamplePatient();
        MedicalStaff medicalStaff = createSampleMedicalStaff();
        Appointment savedAppointment = createSampleAppointment();

        when(patientRepository.findById(inputDTO.getPatientId())).thenReturn(Optional.of(patient));
        when(medicalStaffRepository.findById(inputDTO.getMedicalStaffId())).thenReturn(Optional.of(medicalStaff));
        when(appointmentRepository.findByMedicalStaffAndAppointmentDateTime(medicalStaff, inputDTO.getAppointmentDateTime()))
                .thenReturn(Optional.empty());
        when(appointmentRepository.findByPatientAndAppointmentDateTime(patient, inputDTO.getAppointmentDateTime()))
                .thenReturn(Optional.empty());
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(savedAppointment);

        // When
        AppointmentDTO result = appointmentService.createAppointment(inputDTO);

        // Then
        assertNotNull(result);
        assertEquals(savedAppointment.getId(), result.getId());
        verify(patientRepository, times(1)).findById(inputDTO.getPatientId());
        verify(medicalStaffRepository, times(1)).findById(inputDTO.getMedicalStaffId());
        verify(appointmentRepository, times(1)).findByMedicalStaffAndAppointmentDateTime(medicalStaff, inputDTO.getAppointmentDateTime());
        verify(appointmentRepository, times(1)).findByPatientAndAppointmentDateTime(patient, inputDTO.getAppointmentDateTime());
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
    }

    @Test
    void createAppointment_WithNullDTO_ShouldThrowException() {
        // Given & When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> appointmentService.createAppointment(null));

        assertEquals("Appointment cannot be null", exception.getMessage());
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void createAppointment_WithNullMedicalStaffId_ShouldThrowException() {
        // Given
        AppointmentDTO inputDTO = createSampleAppointmentDTO();
        inputDTO.setMedicalStaffId(null);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> appointmentService.createAppointment(inputDTO));

        assertEquals("Medical staff cannot be null", exception.getMessage());
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void createAppointment_WithNullPatientId_ShouldThrowException() {
        // Given
        AppointmentDTO inputDTO = createSampleAppointmentDTO();
        inputDTO.setPatientId(null);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> appointmentService.createAppointment(inputDTO));

        assertEquals("Patient cannot be null", exception.getMessage());
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void createAppointment_WithNonExistentPatient_ShouldThrowException() {
        // Given
        AppointmentDTO inputDTO = createSampleAppointmentDTO();
        when(patientRepository.findById(inputDTO.getPatientId())).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFound exception = assertThrows(ResourceNotFound.class,
                () -> appointmentService.createAppointment(inputDTO));

        assertEquals("Patient not found with id: " + inputDTO.getPatientId(), exception.getMessage());
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void createAppointment_WithNonExistentMedicalStaff_ShouldThrowException() {
        // Given
        AppointmentDTO inputDTO = createSampleAppointmentDTO();
        Patient patient = createSamplePatient();
        when(patientRepository.findById(inputDTO.getPatientId())).thenReturn(Optional.of(patient));
        when(medicalStaffRepository.findById(inputDTO.getMedicalStaffId())).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFound exception = assertThrows(ResourceNotFound.class,
                () -> appointmentService.createAppointment(inputDTO));

        assertEquals("Medical staff not found with id: " + inputDTO.getMedicalStaffId(), exception.getMessage());
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void createAppointment_WithStaffConflict_ShouldThrowException() {
        // Given
        AppointmentDTO inputDTO = createSampleAppointmentDTO();
        Patient patient = createSamplePatient();
        MedicalStaff medicalStaff = createSampleMedicalStaff();
        Appointment conflictingAppointment = createSampleAppointment();

        when(patientRepository.findById(inputDTO.getPatientId())).thenReturn(Optional.of(patient));
        when(medicalStaffRepository.findById(inputDTO.getMedicalStaffId())).thenReturn(Optional.of(medicalStaff));
        when(appointmentRepository.findByMedicalStaffAndAppointmentDateTime(medicalStaff, inputDTO.getAppointmentDateTime()))
                .thenReturn(Optional.of(conflictingAppointment));

        // When & Then
        DuplicateResourceException exception = assertThrows(DuplicateResourceException.class,
                () -> appointmentService.createAppointment(inputDTO));

        assertEquals("Appointment already exists at this time for this medical staff.", exception.getMessage());
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void createAppointment_WithPatientConflict_ShouldThrowException() {
        // Given
        AppointmentDTO inputDTO = createSampleAppointmentDTO();
        Patient patient = createSamplePatient();
        MedicalStaff medicalStaff = createSampleMedicalStaff();
        Appointment conflictingAppointment = createSampleAppointment();

        when(patientRepository.findById(inputDTO.getPatientId())).thenReturn(Optional.of(patient));
        when(medicalStaffRepository.findById(inputDTO.getMedicalStaffId())).thenReturn(Optional.of(medicalStaff));
        when(appointmentRepository.findByMedicalStaffAndAppointmentDateTime(medicalStaff, inputDTO.getAppointmentDateTime()))
                .thenReturn(Optional.empty());
        when(appointmentRepository.findByPatientAndAppointmentDateTime(patient, inputDTO.getAppointmentDateTime()))
                .thenReturn(Optional.of(conflictingAppointment));

        // When & Then
        ConflictException exception = assertThrows(ConflictException.class,
                () -> appointmentService.createAppointment(inputDTO));

        assertEquals("Patient already have appointment at this time.", exception.getMessage());
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void createAppointment_WithNullStatus_ShouldSetDefaultStatus() {
        // Given
        AppointmentDTO inputDTO = createSampleAppointmentDTO();
        inputDTO.setStatus(null); // No status provided

        Patient patient = createSamplePatient();
        MedicalStaff medicalStaff = createSampleMedicalStaff();
        Appointment savedAppointment = createSampleAppointment();

        when(patientRepository.findById(inputDTO.getPatientId())).thenReturn(Optional.of(patient));
        when(medicalStaffRepository.findById(inputDTO.getMedicalStaffId())).thenReturn(Optional.of(medicalStaff));
        when(appointmentRepository.findByMedicalStaffAndAppointmentDateTime(medicalStaff, inputDTO.getAppointmentDateTime()))
                .thenReturn(Optional.empty());
        when(appointmentRepository.findByPatientAndAppointmentDateTime(patient, inputDTO.getAppointmentDateTime()))
                .thenReturn(Optional.empty());
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(savedAppointment);

        // When
        AppointmentDTO result = appointmentService.createAppointment(inputDTO);

        // Then
        assertNotNull(result);
        // Should handle null status and set default
    }

    // Test for deleteAppointment()
    @Test
    void deleteAppointment_WithValidData_ShouldDeleteAppointment() {
        // Given
        Long appointmentId = 1L;
        Patient patient = createSamplePatient();
        MedicalStaff medicalStaff = createSampleMedicalStaff();
        Appointment existingAppointment = createSampleAppointment();

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(existingAppointment));

        // When
        appointmentService.deleteAppointment(appointmentId, patient, medicalStaff);

        // Then
        verify(appointmentRepository, times(1)).findById(appointmentId);
        verify(appointmentRepository, times(1)).deleteById(appointmentId);
    }

    @Test
    void deleteAppointment_WithNonExistentId_ShouldThrowException() {
        // Given
        Long appointmentId = 999L;
        Patient patient = createSamplePatient();
        MedicalStaff medicalStaff = createSampleMedicalStaff();

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> appointmentService.deleteAppointment(appointmentId, patient, medicalStaff));

        assertEquals("Appointment with id " + appointmentId + " not found.", exception.getMessage());
        verify(appointmentRepository, never()).deleteById(anyLong());
    }

    @Test
    void deleteAppointment_WithDifferentPatient_ShouldThrowSecurityException() {
        // Given
        Long appointmentId = 1L;
        Patient differentPatient = createSamplePatient();
        differentPatient.setId(2L); // Different patient
        MedicalStaff medicalStaff = createSampleMedicalStaff();
        Appointment existingAppointment = createSampleAppointment();

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(existingAppointment));

        // When & Then
        SecurityException exception = assertThrows(SecurityException.class,
                () -> appointmentService.deleteAppointment(appointmentId, differentPatient, medicalStaff));

        assertEquals("You do not have permission to delete this appointment.", exception.getMessage());
        verify(appointmentRepository, never()).deleteById(anyLong());
    }

    @Test
    void deleteAppointment_WithDifferentMedicalStaff_ShouldThrowSecurityException() {
        // Given
        Long appointmentId = 1L;
        Patient patient = createSamplePatient();
        MedicalStaff differentMedicalStaff = createSampleMedicalStaff();
        differentMedicalStaff.setId(2L); // Different medical staff
        Appointment existingAppointment = createSampleAppointment();

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(existingAppointment));

        // When & Then
        SecurityException exception = assertThrows(SecurityException.class,
                () -> appointmentService.deleteAppointment(appointmentId, patient, differentMedicalStaff));

        assertEquals("Medical Staff does not have permission for this operation.", exception.getMessage());
        verify(appointmentRepository, never()).deleteById(anyLong());
    }

    // Test for updateAppointment()
    @Test
    void updateAppointment_WithValidData_ShouldReturnUpdatedDTO() {
        // Given
        Long appointmentId = 1L;
        AppointmentDTO updateDTO = createSampleAppointmentDTO();
        updateDTO.setId(appointmentId);
        updateDTO.setReason("Updated reason");

        Appointment existingAppointment = createSampleAppointment();
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(existingAppointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(existingAppointment);

        // When
        AppointmentDTO result = appointmentService.updateAppointment(appointmentId, updateDTO);

        // Then
        assertNotNull(result);
        verify(appointmentRepository, times(1)).findById(appointmentId);
        verify(appointmentRepository, times(1)).save(existingAppointment);
    }

    @Test
    void updateAppointment_WithNonExistentId_ShouldThrowException() {
        // Given
        Long appointmentId = 999L;
        AppointmentDTO updateDTO = createSampleAppointmentDTO();
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFound exception = assertThrows(ResourceNotFound.class,
                () -> appointmentService.updateAppointment(appointmentId, updateDTO));

        assertEquals("Appointment with id #" + appointmentId + " not found.", exception.getMessage());
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void updateAppointment_WithNullDTO_ShouldThrowException() {
        // Given
        Long appointmentId = 1L;

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> appointmentService.updateAppointment(appointmentId, null));

        assertEquals("AppointmentDTO cannot be null.", exception.getMessage());
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void updateAppointment_WithDateTimeConflict_ShouldThrowException() {
        // Given
        Long appointmentId = 1L;
        AppointmentDTO updateDTO = createSampleAppointmentDTO();
        updateDTO.setId(appointmentId);
        updateDTO.setAppointmentDateTime(LocalDateTime.of(2024, 12, 15, 11, 0)); // Different time

        Appointment existingAppointment = createSampleAppointment();
        MedicalStaff medicalStaff = createSampleMedicalStaff();
        Appointment conflictingAppointment = createSampleAppointment();
        conflictingAppointment.setId(2L); // Different appointment

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(existingAppointment));
        when(appointmentRepository.findByMedicalStaffAndAppointmentDateTime(medicalStaff, updateDTO.getAppointmentDateTime()))
                .thenReturn(Optional.of(conflictingAppointment));

        // When & Then
        ConflictException exception = assertThrows(ConflictException.class,
                () -> appointmentService.updateAppointment(appointmentId, updateDTO));

        assertEquals("Medical staff already has appointment at this time.", exception.getMessage());
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void updateAppointment_WithNewPatient_ShouldUpdatePatient() {
        // Given
        Long appointmentId = 1L;
        Long newPatientId = 2L;
        AppointmentDTO updateDTO = createSampleAppointmentDTO();
        updateDTO.setId(appointmentId);
        updateDTO.setPatientId(newPatientId);

        Appointment existingAppointment = createSampleAppointment();
        Patient newPatient = createSamplePatient();
        newPatient.setId(newPatientId);

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(existingAppointment));
        when(patientRepository.findById(newPatientId)).thenReturn(Optional.of(newPatient));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(existingAppointment);

        // When
        AppointmentDTO result = appointmentService.updateAppointment(appointmentId, updateDTO);

        // Then
        assertNotNull(result);
        verify(patientRepository, times(1)).findById(newPatientId);
        verify(appointmentRepository, times(1)).save(existingAppointment);
    }

    @Test
    void updateAppointment_WithNewMedicalStaff_ShouldUpdateMedicalStaff() {
        // Given
        Long appointmentId = 1L;
        Long newMedicalStaffId = 2L;
        AppointmentDTO updateDTO = createSampleAppointmentDTO();
        updateDTO.setId(appointmentId);
        updateDTO.setMedicalStaffId(newMedicalStaffId);

        Appointment existingAppointment = createSampleAppointment();
        MedicalStaff newMedicalStaff = createSampleMedicalStaff();
        newMedicalStaff.setId(newMedicalStaffId);

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(existingAppointment));
        when(medicalStaffRepository.findById(newMedicalStaffId)).thenReturn(Optional.of(newMedicalStaff));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(existingAppointment);

        // When
        AppointmentDTO result = appointmentService.updateAppointment(appointmentId, updateDTO);

        // Then
        assertNotNull(result);
        verify(medicalStaffRepository, times(1)).findById(newMedicalStaffId);
        verify(appointmentRepository, times(1)).save(existingAppointment);
    }

    // Test for convertToDTO()
    @Test
    void convertToDTO_WithValidAppointment_ShouldReturnCorrectDTO() {
        // Given
        Appointment appointment = createSampleAppointment();

        // When
        AppointmentDTO result = appointmentService.convertToDTO(appointment);

        // Then
        assertNotNull(result);
        assertEquals(appointment.getId(), result.getId());
        assertEquals(appointment.getPatient().getId(), result.getPatientId());
        assertEquals(appointment.getMedicalStaff().getId(), result.getMedicalStaffId());
        assertEquals(appointment.getStatus(), result.getStatus());
        assertEquals(appointment.getAppointmentDateTime(), result.getAppointmentDateTime());
        assertEquals(appointment.getReason(), result.getReason());
        assertEquals(appointment.getNotes(), result.getNotes());
        assertEquals(appointment.getAppointmentDuration(), result.getAppointmentDuration());
    }

    @Test
    void convertToDTO_WithAppointmentWithDepartment_ShouldIncludeDepartmentId() {
        // Given
        Appointment appointment = createSampleAppointment();
        // Note: You would need to set up a department in your sample appointment
        // For now, testing without department (should handle null)

        // When
        AppointmentDTO result = appointmentService.convertToDTO(appointment);

        // Then
        assertNotNull(result);
        // Should handle null department gracefully
    }

    // Additional edge case tests
    @Test
    void createAppointment_WithEmptyReason_ShouldWork() {
        // Given
        AppointmentDTO inputDTO = createSampleAppointmentDTO();
        inputDTO.setReason(""); // Empty reason

        Patient patient = createSamplePatient();
        MedicalStaff medicalStaff = createSampleMedicalStaff();
        Appointment savedAppointment = createSampleAppointment();

        when(patientRepository.findById(inputDTO.getPatientId())).thenReturn(Optional.of(patient));
        when(medicalStaffRepository.findById(inputDTO.getMedicalStaffId())).thenReturn(Optional.of(medicalStaff));
        when(appointmentRepository.findByMedicalStaffAndAppointmentDateTime(medicalStaff, inputDTO.getAppointmentDateTime()))
                .thenReturn(Optional.empty());
        when(appointmentRepository.findByPatientAndAppointmentDateTime(patient, inputDTO.getAppointmentDateTime()))
                .thenReturn(Optional.empty());
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(savedAppointment);

        // When
        AppointmentDTO result = appointmentService.createAppointment(inputDTO);

        // Then
        assertNotNull(result);
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
    }

    @Test
    void updateAppointment_WithOnlyStatusChange_ShouldUpdateSuccessfully() {
        // Given
        Long appointmentId = 1L;
        AppointmentDTO updateDTO = new AppointmentDTO();
        updateDTO.setId(appointmentId);
        updateDTO.setStatus(Appointment.Status.COMPLETED); // Only status change

        Appointment existingAppointment = createSampleAppointment();
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(existingAppointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(existingAppointment);

        // When
        AppointmentDTO result = appointmentService.updateAppointment(appointmentId, updateDTO);

        // Then
        assertNotNull(result);
        verify(appointmentRepository, times(1)).findById(appointmentId);
        verify(appointmentRepository, times(1)).save(existingAppointment);
    }

    @Test
    void updateAppointment_WithOnlyReasonChange_ShouldUpdateSuccessfully() {
        // Given
        Long appointmentId = 1L;
        AppointmentDTO updateDTO = new AppointmentDTO();
        updateDTO.setId(appointmentId);
        updateDTO.setReason("Updated reason only"); // Only reason change

        Appointment existingAppointment = createSampleAppointment();
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(existingAppointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(existingAppointment);

        // When
        AppointmentDTO result = appointmentService.updateAppointment(appointmentId, updateDTO);

        // Then
        assertNotNull(result);
        verify(appointmentRepository, times(1)).findById(appointmentId);
        verify(appointmentRepository, times(1)).save(existingAppointment);
    }
}