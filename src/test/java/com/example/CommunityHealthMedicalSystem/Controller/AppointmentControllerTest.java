package com.example.CommunityHealthMedicalSystem.Controller;

import com.example.CommunityHealthMedicalSystem.Controller.AppointmentController;
import com.example.CommunityHealthMedicalSystem.DTO.AppointmentDTO;
import com.example.CommunityHealthMedicalSystem.Exception.*;
import com.example.CommunityHealthMedicalSystem.Exception.IllegalArgumentException;
import com.example.CommunityHealthMedicalSystem.Exception.SecurityException;
import com.example.CommunityHealthMedicalSystem.Model.Appointment;
import com.example.CommunityHealthMedicalSystem.Model.MedicalStaff;
import com.example.CommunityHealthMedicalSystem.Model.Patient;
import com.example.CommunityHealthMedicalSystem.Repository.MedicalStaffRepository;
import com.example.CommunityHealthMedicalSystem.Repository.PatientRepository;
import com.example.CommunityHealthMedicalSystem.Service.AppointmentServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AppointmentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AppointmentServiceImpl appointmentService;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private MedicalStaffRepository medicalStaffRepository;

    @InjectMocks
    private AppointmentController appointmentController;

    private ObjectMapper objectMapper;
    private Patient samplePatient;
    private MedicalStaff sampleMedicalStaff;
    private Appointment sampleAppointment;
    private AppointmentDTO sampleAppointmentDTO;

    @BeforeEach
    void setUp() {
        // Remove the duplicate mockMvc setup - keep only one
        mockMvc = MockMvcBuilders.standaloneSetup(appointmentController)
                .setControllerAdvice(new GlobalExceptionHandler()) // Add this
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        // Initialize sample data
        samplePatient = new Patient();
        samplePatient.setId(1L);
        samplePatient.setFirstName("John");
        samplePatient.setLastName("Doe");
        samplePatient.setEmail("john.doe@example.com");

        sampleMedicalStaff = new MedicalStaff();
        sampleMedicalStaff.setId(1L);
        sampleMedicalStaff.setFirstName("Dr. Jane");
        sampleMedicalStaff.setLastName("Smith");
        sampleMedicalStaff.setEmail("jane.smith@hospital.com");

        sampleAppointment = new Appointment();
        sampleAppointment.setId(1L);
        sampleAppointment.setPatient(samplePatient);
        sampleAppointment.setMedicalStaff(sampleMedicalStaff);
        sampleAppointment.setAppointmentDateTime(LocalDateTime.now().plusDays(1)); // Future date
        sampleAppointment.setReason("Regular checkup");
        sampleAppointment.setStatus(Appointment.Status.SCHEDULED);
        sampleAppointment.setNotes("Some notes"); // Add notes
        sampleAppointment.setAppointmentDuration(30); // Add duration

        sampleAppointmentDTO = new AppointmentDTO();
        sampleAppointmentDTO.setId(1L);
        sampleAppointmentDTO.setPatientId(1L);
        sampleAppointmentDTO.setMedicalStaffId(1L);
        sampleAppointmentDTO.setAppointmentDateTime(LocalDateTime.now().plusDays(1)); // Future date
        sampleAppointmentDTO.setReason("Regular checkup");
        sampleAppointmentDTO.setStatus(Appointment.Status.SCHEDULED);
        sampleAppointmentDTO.setNotes("Some notes"); // Add notes
        sampleAppointmentDTO.setAppointmentDuration(30); // Add duration
    }

    // GET /api/appointments - GetAllAppointments
    @Test
    void getAllAppointments_ShouldReturnAppointmentsList() throws Exception {
        // Given
        List<Appointment> appointments = Arrays.asList(sampleAppointment);
        when(appointmentService.getAllAppointments()).thenReturn(appointments);

        // When & Then
        mockMvc.perform(get("/api/appointments")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].reason").value("Regular checkup"));

        verify(appointmentService, times(1)).getAllAppointments();
    }

    @Test
    void getAllAppointments_WhenServiceThrowsException_ShouldReturnInternalServerError() throws Exception {
        // Given
        when(appointmentService.getAllAppointments()).thenThrow(new RuntimeException("Database error"));

        // When & Then
        mockMvc.perform(get("/api/appointments")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(appointmentService, times(1)).getAllAppointments();
    }

    // GET /api/appointments/{id} - GetAppointmentById
    @Test
    void getAppointmentById_WithValidId_ShouldReturnAppointment() throws Exception {
        // Given
        when(appointmentService.getAppointmentById(1L)).thenReturn(Optional.of(sampleAppointment));

        // When & Then
        mockMvc.perform(get("/api/appointments/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.reason").value("Regular checkup"));

        verify(appointmentService, times(1)).getAppointmentById(1L);
    }

    @Test
    void getAppointmentById_WithNonExistentId_ShouldReturnNotFound() throws Exception {
        // Given
        when(appointmentService.getAppointmentById(999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/appointments/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(appointmentService, times(1)).getAppointmentById(999L);
    }

    // GET /api/appointments/reason/{reason} - GetAppointmentByReason
    @Test
    void getAppointmentByReason_WithValidReason_ShouldReturnAppointments() throws Exception {
        // Given
        List<Appointment> appointments = Arrays.asList(sampleAppointment);
        when(appointmentService.getAppointmentByReason("Regular checkup")).thenReturn(appointments);

        // When & Then
        mockMvc.perform(get("/api/appointments/reason/Regular checkup")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].reason").value("Regular checkup"));

        verify(appointmentService, times(1)).getAppointmentByReason("Regular checkup");
    }

    @Test
    void getAppointmentByReason_WithNonExistentReason_ShouldReturnNotFound() throws Exception {
        // Given
        when(appointmentService.getAppointmentByReason("Unknown")).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/api/appointments/reason/Unknown")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(appointmentService, times(1)).getAppointmentByReason("Unknown");
    }

    // GET /api/appointments/status/{status} - GetAppointmentByStatus
    @Test
    void getAppointmentByStatus_WithValidStatus_ShouldReturnAppointments() throws Exception {
        // Given
        List<Appointment> appointments = Arrays.asList(sampleAppointment);
        when(appointmentService.getAppointmentByStatus(Appointment.Status.SCHEDULED)).thenReturn(appointments);

        // When & Then
        mockMvc.perform(get("/api/appointments/status/SCHEDULED")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].status").value("SCHEDULED"));

        verify(appointmentService, times(1)).getAppointmentByStatus(Appointment.Status.SCHEDULED);
    }

    @Test
    void getAppointmentByStatus_WithNonExistentStatus_ShouldReturnNotFound() throws Exception {
        // Given
        when(appointmentService.getAppointmentByStatus(Appointment.Status.COMPLETED)).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/api/appointments/status/COMPLETED")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(appointmentService, times(1)).getAppointmentByStatus(Appointment.Status.COMPLETED);
    }

    // GET /api/appointments/by-patient/{patientId} - GetAppointmentByPatientId
    @Test
    void getAppointmentByPatientId_WithValidId_ShouldReturnAppointments() throws Exception {
        // Given
        List<Appointment> appointments = Arrays.asList(sampleAppointment);
        when(appointmentService.getAppointmentByPatientId(1L)).thenReturn(appointments);

        // When & Then
        mockMvc.perform(get("/api/appointments/by-patient/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));

        verify(appointmentService, times(1)).getAppointmentByPatientId(1L);
    }

    @Test
    void getAppointmentByPatientId_WithSecurityException_ShouldReturnForbidden() throws Exception {
        // Given
        when(appointmentService.getAppointmentByPatientId(1L))
                .thenThrow(new SecurityException("Access denied"));

        // When & Then
        mockMvc.perform(get("/api/appointments/by-patient/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verify(appointmentService, times(1)).getAppointmentByPatientId(1L);
    }

    @Test
    void getAppointmentByPatientId_WithResourceNotFound_ShouldReturnNotFound() throws Exception {
        // Given
        when(appointmentService.getAppointmentByPatientId(1L))
                .thenThrow(new ResourceNotFound("Patient not found"));

        // When & Then
        mockMvc.perform(get("/api/appointments/by-patient/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(appointmentService, times(1)).getAppointmentByPatientId(1L);
    }

    // GET /api/appointments/by-medical-id/{medicalId} - GetAppointmentByMedicalId
    @Test
    void getAppointmentByMedicalId_WithValidId_ShouldReturnAppointments() throws Exception {
        // Given
        List<Appointment> appointments = Arrays.asList(sampleAppointment);
        when(appointmentService.getAppointmentByMedicalId(1L)).thenReturn(appointments);

        // When & Then
        mockMvc.perform(get("/api/appointments/by-medical-id/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));

        verify(appointmentService, times(1)).getAppointmentByMedicalId(1L);
    }

    @Test
    void getAppointmentByMedicalId_WithNonExistentId_ShouldReturnNotFound() throws Exception {
        // Given
        when(appointmentService.getAppointmentByMedicalId(999L)).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/api/appointments/by-medical-id/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(appointmentService, times(1)).getAppointmentByMedicalId(999L);
    }

    // GET /api/appointments/by-department-id/{departmentId} - GetAppointmentByDepartmentId
    @Test
    void getAppointmentByDepartmentId_WithValidId_ShouldReturnAppointments() throws Exception {
        // Given
        List<Appointment> appointments = Arrays.asList(sampleAppointment);
        when(appointmentService.getAppointmentByDepartmentId(1L)).thenReturn(appointments);

        // When & Then
        mockMvc.perform(get("/api/appointments/by-department-id/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));

        verify(appointmentService, times(1)).getAppointmentByDepartmentId(1L);
    }

    @Test
    void getAppointmentByDepartmentId_WithNonExistentId_ShouldReturnNotFound() throws Exception {
        // Given
        when(appointmentService.getAppointmentByDepartmentId(999L)).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/api/appointments/by-department-id/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(appointmentService, times(1)).getAppointmentByDepartmentId(999L);
    }

    // GET /api/appointments/by-date-range - GetAppointmentByDateRange
    @Test
    void getAppointmentByDateRange_WithValidDates_ShouldReturnAppointments() throws Exception {
        // Given
        LocalDateTime startDate = LocalDateTime.of(2024, 12, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2024, 12, 31, 23, 59);
        List<Appointment> appointments = Arrays.asList(sampleAppointment);

        when(appointmentService.getAppointmentsByDateRange(startDate, endDate)).thenReturn(appointments);

        // When & Then
        mockMvc.perform(get("/api/appointments/by-date-range")
                        .param("startDate", "2024-12-01T00:00:00")
                        .param("endDate", "2024-12-31T23:59:00")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));

        verify(appointmentService, times(1)).getAppointmentsByDateRange(startDate, endDate);
    }

    @Test
    void getAppointmentByDateRange_WhenServiceThrowsException_ShouldReturnInternalServerError() throws Exception {
        // Given
        LocalDateTime startDate = LocalDateTime.of(2024, 12, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2024, 12, 31, 23, 59);

        when(appointmentService.getAppointmentsByDateRange(startDate, endDate))
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        mockMvc.perform(get("/api/appointments/by-date-range")
                        .param("startDate", "2024-12-01T00:00:00")
                        .param("endDate", "2024-12-31T23:59:00")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(appointmentService, times(1)).getAppointmentsByDateRange(startDate, endDate);
    }

    // POST /api/appointments - CreateAppointment
    @Test
    void createAppointment_WithValidData_ShouldReturnCreatedAppointment() throws Exception {
        // Given
        when(appointmentService.createAppointment(any(AppointmentDTO.class))).thenReturn(sampleAppointmentDTO);

        // When & Then
        mockMvc.perform(post("/api/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleAppointmentDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.reason").value("Regular checkup"));

        verify(appointmentService, times(1)).createAppointment(any(AppointmentDTO.class));
    }

    @Test
    void createAppointment_WithDuplicateResource_ShouldReturnBadRequest() throws Exception {
        // Given
        when(appointmentService.createAppointment(any(AppointmentDTO.class)))
                .thenThrow(new DuplicateResourceException("Duplicate appointment"));

        // When & Then
        mockMvc.perform(post("/api/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleAppointmentDTO)))
                .andExpect(status().isBadRequest());

        verify(appointmentService, times(1)).createAppointment(any(AppointmentDTO.class));
    }

    @Test
    void createAppointment_WithResourceNotFound_ShouldReturnNotFound() throws Exception {
        // Given
        when(appointmentService.createAppointment(any(AppointmentDTO.class)))
                .thenThrow(new ResourceNotFound("Patient not found"));

        // When & Then
        mockMvc.perform(post("/api/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleAppointmentDTO)))
                .andExpect(status().isNotFound());

        verify(appointmentService, times(1)).createAppointment(any(AppointmentDTO.class));
    }

    @Test
    void createAppointment_WithConflict_ShouldReturnConflict() throws Exception {
        // Given
        when(appointmentService.createAppointment(any(AppointmentDTO.class)))
                .thenThrow(new ConflictException("Time conflict"));

        // When & Then
        mockMvc.perform(post("/api/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleAppointmentDTO)))
                .andExpect(status().isConflict());

        verify(appointmentService, times(1)).createAppointment(any(AppointmentDTO.class));
    }

    @Test
    void createAppointment_WithIllegalArgument_ShouldReturnBadRequest() throws Exception {
        // Given
        when(appointmentService.createAppointment(any(AppointmentDTO.class)))
                .thenThrow(new IllegalArgumentException("Invalid input"));

        // When & Then
        mockMvc.perform(post("/api/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleAppointmentDTO)))
                .andExpect(status().isBadRequest());

        verify(appointmentService, times(1)).createAppointment(any(AppointmentDTO.class));
    }

    // PATCH /api/appointments/{id}/status - UpdateAppointmentStatus
    @Test
    void updateAppointmentStatus_WithValidData_ShouldReturnUpdatedAppointment() throws Exception {
        // Given
        when(appointmentService.updateAppointment(eq(1L), any(AppointmentDTO.class)))
                .thenReturn(sampleAppointmentDTO);

        // When & Then
        mockMvc.perform(patch("/api/appointments/1/status")
                        .param("status", "COMPLETED")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(appointmentService, times(1)).updateAppointment(eq(1L), any(AppointmentDTO.class));
    }

    @Test
    void updateAppointmentStatus_WithNonExistentAppointment_ShouldReturnNotFound() throws Exception {
        // Given
        when(appointmentService.updateAppointment(eq(999L), any(AppointmentDTO.class)))
                .thenThrow(new ResourceNotFound("Appointment not found"));

        // When & Then
        mockMvc.perform(patch("/api/appointments/999/status")
                        .param("status", "COMPLETED")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(appointmentService, times(1)).updateAppointment(eq(999L), any(AppointmentDTO.class));
    }

    @Test
    void updateAppointmentStatus_WithInvalidStatus_ShouldReturnBadRequest() throws Exception {
        // Given
        when(appointmentService.updateAppointment(eq(1L), any(AppointmentDTO.class)))
                .thenThrow(new IllegalArgumentException("Invalid status"));

        // When & Then
        mockMvc.perform(patch("/api/appointments/1/status")
                        .param("status", "COMPLETED")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(appointmentService, times(1)).updateAppointment(eq(1L), any(AppointmentDTO.class));
    }

    // PUT /api/appointments/{id} - UpdateAppointment
    @Test
    void updateAppointment_WithValidData_ShouldReturnUpdatedAppointment() throws Exception {
        // Given
        when(appointmentService.updateAppointment(eq(1L), any(AppointmentDTO.class)))
                .thenReturn(sampleAppointmentDTO);

        // When & Then
        mockMvc.perform(put("/api/appointments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleAppointmentDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(appointmentService, times(1)).updateAppointment(eq(1L), any(AppointmentDTO.class));
    }

    @Test
    void updateAppointment_WithNonExistentAppointment_ShouldReturnNotFound() throws Exception {
        // Given
        when(appointmentService.updateAppointment(eq(999L), any(AppointmentDTO.class)))
                .thenThrow(new ResourceNotFound("Appointment not found"));

        // When & Then
        mockMvc.perform(put("/api/appointments/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleAppointmentDTO)))
                .andExpect(status().isNotFound());

        verify(appointmentService, times(1)).updateAppointment(eq(999L), any(AppointmentDTO.class));
    }

    @Test
    void updateAppointment_WithIllegalArgument_ShouldReturnBadRequest() throws Exception {
        // Given
        when(appointmentService.updateAppointment(eq(1L), any(AppointmentDTO.class)))
                .thenThrow(new IllegalArgumentException("Invalid input"));

        // When & Then
        mockMvc.perform(put("/api/appointments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleAppointmentDTO)))
                .andExpect(status().isBadRequest());

        verify(appointmentService, times(1)).updateAppointment(eq(1L), any(AppointmentDTO.class));
    }

    @Test
    void updateAppointment_WithConflict_ShouldReturnConflict() throws Exception {
        // Given
        when(appointmentService.updateAppointment(eq(1L), any(AppointmentDTO.class)))
                .thenThrow(new ConflictException("Time conflict"));

        // When & Then
        mockMvc.perform(put("/api/appointments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleAppointmentDTO)))
                .andExpect(status().isConflict());

        verify(appointmentService, times(1)).updateAppointment(eq(1L), any(AppointmentDTO.class));
    }

    // DELETE /api/appointments/{id} - DeleteAppointment
    @Test
    void deleteAppointment_WithValidData_ShouldReturnNoContent() throws Exception {
        // Given
        when(patientRepository.findById(1L)).thenReturn(Optional.of(samplePatient));
        when(medicalStaffRepository.findById(1L)).thenReturn(Optional.of(sampleMedicalStaff));
        doNothing().when(appointmentService).deleteAppointment(1L, samplePatient, sampleMedicalStaff);

        // When & Then
        mockMvc.perform(delete("/api/appointments/1")
                        .param("patientId", "1")
                        .param("medicalStaffId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(patientRepository, times(1)).findById(1L);
        verify(medicalStaffRepository, times(1)).findById(1L);
        verify(appointmentService, times(1)).deleteAppointment(1L, samplePatient, sampleMedicalStaff);
    }

    @Test
    void deleteAppointment_WithNonExistentPatient_ShouldReturnNotFound() throws Exception {
        // Given
        when(patientRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(delete("/api/appointments/1")
                        .param("patientId", "999")
                        .param("medicalStaffId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(patientRepository, times(1)).findById(999L);
        verify(medicalStaffRepository, never()).findById(anyLong());
        verify(appointmentService, never()).deleteAppointment(anyLong(), any(Patient.class), any(MedicalStaff.class));
    }

    @Test
    void deleteAppointment_WithNonExistentMedicalStaff_ShouldReturnNotFound() throws Exception {
        // Given
        when(patientRepository.findById(1L)).thenReturn(Optional.of(samplePatient));
        when(medicalStaffRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(delete("/api/appointments/1")
                        .param("patientId", "1")
                        .param("medicalStaffId", "999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(patientRepository, times(1)).findById(1L);
        verify(medicalStaffRepository, times(1)).findById(999L);
        verify(appointmentService, never()).deleteAppointment(anyLong(), any(Patient.class), any(MedicalStaff.class));
    }

    @Test
    void deleteAppointment_WithIllegalArgument_ShouldReturnBadRequest() throws Exception {
        // Given
        when(patientRepository.findById(1L)).thenReturn(Optional.of(samplePatient));
        when(medicalStaffRepository.findById(1L)).thenReturn(Optional.of(sampleMedicalStaff));
        doThrow(new IllegalArgumentException("Invalid operation"))
                .when(appointmentService).deleteAppointment(1L, samplePatient, sampleMedicalStaff);

        // When & Then
        mockMvc.perform(delete("/api/appointments/1")
                        .param("patientId", "1")
                        .param("medicalStaffId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(patientRepository, times(1)).findById(1L);
        verify(medicalStaffRepository, times(1)).findById(1L);
        verify(appointmentService, times(1)).deleteAppointment(1L, samplePatient, sampleMedicalStaff);
    }

    // Additional Tests for Edge Cases

    @Test
    void createAppointment_WithInvalidJSON_ShouldReturnBadRequest() throws Exception {
        // Given invalid JSON
        String invalidJson = "{ invalid json }";

        // When & Then
        mockMvc.perform(post("/api/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());

        verify(appointmentService, never()).createAppointment(any(AppointmentDTO.class));
    }

    @Test
    void updateAppointment_WithInvalidJSON_ShouldReturnBadRequest() throws Exception {
        // Given invalid JSON
        String invalidJson = "{ invalid json }";

        // When & Then
        mockMvc.perform(put("/api/appointments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());

        verify(appointmentService, never()).updateAppointment(anyLong(), any(AppointmentDTO.class));
    }

    @Test
    void getAppointmentByDateRange_WithInvalidDateParameters_ShouldReturnBadRequest() throws Exception {
        // When & Then - Missing required parameters
        mockMvc.perform(get("/api/appointments/by-date-range")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(appointmentService, never()).getAppointmentsByDateRange(any(), any());
    }
}