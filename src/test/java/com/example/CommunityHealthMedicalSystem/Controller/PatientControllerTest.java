package com.example.CommunityHealthMedicalSystem.Controller;

import com.example.CommunityHealthMedicalSystem.DTO.PatientDTO;
import com.example.CommunityHealthMedicalSystem.Exception.ConflictException;
import com.example.CommunityHealthMedicalSystem.Exception.ResourceNotFound;
import com.example.CommunityHealthMedicalSystem.Model.Patient;
import com.example.CommunityHealthMedicalSystem.Service.PatientServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class PatientControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PatientServiceImpl patientService;

    @InjectMocks
    private PatientController patientController;

    private ObjectMapper objectMapper;
    private Patient patient;
    private PatientDTO patientDTO;
    private List<Patient> patientList;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(patientController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // Setup test data
        patient = new Patient();
        patient.setId(1L);
        patient.setFirstName("John");
        patient.setLastName("Doe");
        patient.setEmail("john.doe@example.com");
        patient.setPhoneNumber("123-456-7890");
        patient.setNationalId("ID123456");
        patient.setDateOfBirth(LocalDate.of(1985, 5, 15));
        patient.setAddress("123 Main St, City, Country");

        patientDTO = new PatientDTO();
        patientDTO.setId(1L);
        patientDTO.setFirstName("John");
        patientDTO.setLastName("Doe");
        patientDTO.setEmail("john.doe@example.com");
        patientDTO.setPhoneNumber("123-456-7890");
        patientDTO.setNationalId("ID123456");
        patientDTO.setDateOfBirth(LocalDate.of(1985, 5, 15));
        patientDTO.setAddress("123 Main St, City, Country");

        Patient patient2 = new Patient();
        patient2.setId(2L);
        patient2.setFirstName("Jane");
        patient2.setLastName("Smith");
        patient2.setEmail("jane.smith@example.com");
        patient2.setPhoneNumber("098-765-4321");
        patient2.setNationalId("ID654321");
        patient2.setDateOfBirth(LocalDate.of(1990, 8, 22));
        patient2.setAddress("456 Oak Ave, Town, Country");

        patientList = Arrays.asList(patient, patient2);
    }

    // Test for GET /api/patients
    @Test
    void getAllPatients_ShouldReturnListOfPatients() throws Exception {
        when(patientService.getAllPatients()).thenReturn(patientList);

        mockMvc.perform(get("/api/patients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].firstName").value("Jane"));

        verify(patientService, times(1)).getAllPatients();
    }

    @Test
    void getAllPatients_WhenEmpty_ShouldReturnEmptyList() throws Exception {
        when(patientService.getAllPatients()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/patients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(patientService, times(1)).getAllPatients();
    }

    // Test for GET /api/patients/{id}
    @Test
    void getPatientById_WhenExists_ShouldReturnPatient() throws Exception {
        when(patientService.findById(1L)).thenReturn(Optional.of(patient));

        mockMvc.perform(get("/api/patients/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));

        verify(patientService, times(1)).findById(1L);
    }

    @Test
    void getPatientById_WhenNotExists_ShouldReturnNotFound() throws Exception {
        when(patientService.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/patients/1"))
                .andExpect(status().isNotFound());

        verify(patientService, times(1)).findById(1L);
    }

    @Test
    void getPatientById_WhenException_ShouldReturnInternalServerError() throws Exception {
        when(patientService.findById(1L)).thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(get("/api/patients/1"))
                .andExpect(status().isInternalServerError());

        verify(patientService, times(1)).findById(1L);
    }

    // Test for POST /api/patients
    @Test
    void createPatient_WithValidData_ShouldReturnCreatedPatient() throws Exception {
        when(patientService.createPatient(any(PatientDTO.class))).thenReturn(patientDTO);

        mockMvc.perform(post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patientDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));

        verify(patientService, times(1)).createPatient(any(PatientDTO.class));
    }

    @Test
    void createPatient_WhenConflict_ShouldReturnConflict() throws Exception {
        when(patientService.createPatient(any(PatientDTO.class)))
                .thenThrow(new ConflictException("Patient already exists"));

        mockMvc.perform(post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patientDTO)))
                .andExpect(status().isConflict());

        verify(patientService, times(1)).createPatient(any(PatientDTO.class));
    }

    @Test
    void createPatient_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        PatientDTO invalidDTO = new PatientDTO();
        // Leave all required fields empty/null to trigger validation errors
        invalidDTO.setFirstName("");
        invalidDTO.setLastName("");
        invalidDTO.setEmail("invalid-email");
        invalidDTO.setPhoneNumber("");
        invalidDTO.setNationalId("");
        invalidDTO.setDateOfBirth(null);
        invalidDTO.setAddress("");

        mockMvc.perform(post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(patientService, never()).createPatient(any(PatientDTO.class));
    }

    // Test for PUT /api/patients/{id}
    @Test
    void updatePatient_WithValidData_ShouldReturnUpdatedPatient() throws Exception {
        when(patientService.updatePatient(eq(1L), any(PatientDTO.class))).thenReturn(patientDTO);

        mockMvc.perform(put("/api/patients/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patientDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));

        verify(patientService, times(1)).updatePatient(eq(1L), any(PatientDTO.class));
    }

    @Test
    void updatePatient_WhenResourceNotFound_ShouldReturnNotFound() throws Exception {
        when(patientService.updatePatient(eq(1L), any(PatientDTO.class)))
                .thenThrow(new ResourceNotFound("Patient not found"));

        mockMvc.perform(put("/api/patients/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patientDTO)))
                .andExpect(status().isNotFound());

        verify(patientService, times(1)).updatePatient(eq(1L), any(PatientDTO.class));
    }

    @Test
    void updatePatient_WhenIllegalArgument_ShouldReturnBadRequest() throws Exception {
        when(patientService.updatePatient(eq(1L), any(PatientDTO.class)))
                .thenThrow(new com.example.CommunityHealthMedicalSystem.Exception.IllegalArgumentException("Invalid argument"));

        mockMvc.perform(put("/api/patients/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patientDTO)))
                .andExpect(status().isBadRequest());

        verify(patientService, times(1)).updatePatient(eq(1L), any(PatientDTO.class));
    }

    @Test
    void updatePatient_WhenConflict_ShouldReturnConflict() throws Exception {
        when(patientService.updatePatient(eq(1L), any(PatientDTO.class)))
                .thenThrow(new ConflictException("Conflict occurred"));

        mockMvc.perform(put("/api/patients/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patientDTO)))
                .andExpect(status().isConflict());

        verify(patientService, times(1)).updatePatient(eq(1L), any(PatientDTO.class));
    }

    @Test
    void updatePatient_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        PatientDTO invalidDTO = new PatientDTO();
        // Leave all required fields empty/null to trigger validation errors
        invalidDTO.setFirstName("");
        invalidDTO.setLastName("");
        invalidDTO.setEmail("invalid-email");
        invalidDTO.setPhoneNumber("");
        invalidDTO.setNationalId("");
        invalidDTO.setDateOfBirth(null);
        invalidDTO.setAddress("");

        mockMvc.perform(put("/api/patients/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(patientService, never()).updatePatient(anyLong(), any(PatientDTO.class));
    }

    // Test for DELETE /api/patients/{id}
    @Test
    void deletePatient_WhenExists_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/patients/1"))
                .andExpect(status().isNoContent());

        verify(patientService, times(1)).deletePatient(1L);
    }

    @Test
    void deletePatient_WhenResourceNotFound_ShouldReturnNotFound() throws Exception {
        doThrow(new ResourceNotFound("Patient not found")).when(patientService).deletePatient(1L);

        mockMvc.perform(delete("/api/patients/1"))
                .andExpect(status().isNotFound());

        verify(patientService, times(1)).deletePatient(1L);
    }

    @Test
    void deletePatient_WhenSecurityException_ShouldReturnForbidden() throws Exception {
        doThrow(new SecurityException("Access denied")).when(patientService).deletePatient(1L);

        mockMvc.perform(delete("/api/patients/1"))
                .andExpect(status().isForbidden());

        verify(patientService, times(1)).deletePatient(1L);
    }

    // Test for GET /api/patients/search
    @Test
    void searchPatients_WithAllParameters_ShouldReturnFilteredPatients() throws Exception {
        when(patientService.searchPatients("John", "Doe", "john.doe@example.com", "123 Main St"))
                .thenReturn(Collections.singletonList(patient));

        mockMvc.perform(get("/api/patients/search")
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .param("email", "john.doe@example.com")
                        .param("address", "123 Main St"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].firstName").value("John"));

        verify(patientService, times(1)).searchPatients("John", "Doe", "john.doe@example.com", "123 Main St");
    }

    @Test
    void searchPatients_WithSomeParameters_ShouldReturnFilteredPatients() throws Exception {
        when(patientService.searchPatients("John", null, null, null))
                .thenReturn(Collections.singletonList(patient));

        mockMvc.perform(get("/api/patients/search")
                        .param("firstName", "John"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].firstName").value("John"));

        verify(patientService, times(1)).searchPatients("John", null, null, null);
    }

    @Test
    void searchPatients_WithNoParameters_ShouldReturnAllPatients() throws Exception {
        when(patientService.searchPatients(null, null, null, null))
                .thenReturn(patientList);

        mockMvc.perform(get("/api/patients/search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(patientService, times(1)).searchPatients(null, null, null, null);
    }

    @Test
    void searchPatients_WhenNoResults_ShouldReturnEmptyList() throws Exception {
        when(patientService.searchPatients("Nonexistent", null, null, null))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/patients/search")
                        .param("firstName", "Nonexistent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(patientService, times(1)).searchPatients("Nonexistent", null, null, null);
    }

    // Additional edge case tests
    @Test
    void createPatient_WithMissingRequiredFields_ShouldReturnBadRequest() throws Exception {
        PatientDTO invalidDTO = new PatientDTO();
        // Only set some fields, leave others null/empty
        invalidDTO.setFirstName("John");
        // Missing lastName, email, phoneNumber, nationalId, dateOfBirth, address

        mockMvc.perform(post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(patientService, never()).createPatient(any(PatientDTO.class));
    }

    @Test
    void updatePatient_WithInvalidEmailFormat_ShouldReturnBadRequest() throws Exception {
        PatientDTO invalidDTO = new PatientDTO();
        invalidDTO.setFirstName("John");
        invalidDTO.setLastName("Doe");
        invalidDTO.setEmail("not-an-email");
        invalidDTO.setPhoneNumber("123-456-7890");
        invalidDTO.setNationalId("ID123456");
        invalidDTO.setDateOfBirth(LocalDate.of(1985, 5, 15));
        invalidDTO.setAddress("123 Main St");

        mockMvc.perform(put("/api/patients/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(patientService, never()).updatePatient(anyLong(), any(PatientDTO.class));
    }

    @Test
    void updatePatient_WithShortFirstName_ShouldReturnBadRequest() throws Exception {
        PatientDTO invalidDTO = new PatientDTO();
        invalidDTO.setFirstName("J"); // Too short, min 2 characters
        invalidDTO.setLastName("Doe");
        invalidDTO.setEmail("john.doe@example.com");
        invalidDTO.setPhoneNumber("123-456-7890");
        invalidDTO.setNationalId("ID123456");
        invalidDTO.setDateOfBirth(LocalDate.of(1985, 5, 15));
        invalidDTO.setAddress("123 Main St");

        mockMvc.perform(put("/api/patients/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(patientService, never()).updatePatient(anyLong(), any(PatientDTO.class));
    }

    @Test
    void createPatient_WithShortLastName_ShouldReturnBadRequest() throws Exception {
        PatientDTO invalidDTO = new PatientDTO();
        invalidDTO.setFirstName("John");
        invalidDTO.setLastName("D"); // Too short, min 2 characters
        invalidDTO.setEmail("john.doe@example.com");
        invalidDTO.setPhoneNumber("123-456-7890");
        invalidDTO.setNationalId("ID123456");
        invalidDTO.setDateOfBirth(LocalDate.of(1985, 5, 15));
        invalidDTO.setAddress("123 Main St");

        mockMvc.perform(post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(patientService, never()).createPatient(any(PatientDTO.class));
    }
}