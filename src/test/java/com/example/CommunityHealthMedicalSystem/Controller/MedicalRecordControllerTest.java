package com.example.CommunityHealthMedicalSystem.Controller;

import com.example.CommunityHealthMedicalSystem.DTO.MedicalRecordDTO;
import com.example.CommunityHealthMedicalSystem.Exception.ConflictException;
import com.example.CommunityHealthMedicalSystem.Exception.DuplicateResourceException;
import com.example.CommunityHealthMedicalSystem.Exception.ResourceNotFound;
import com.example.CommunityHealthMedicalSystem.Model.MedicalRecord;
import com.example.CommunityHealthMedicalSystem.Service.MedicalRecordServiceImpl;
import com.example.CommunityHealthMedicalSystem.Service.MedicalStaffServiceImpl;
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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class MedicalRecordControllerTest {

    @Mock
    private MedicalRecordServiceImpl medicalService;

    @Mock
    private MedicalStaffServiceImpl medicalStaffService;

    @InjectMocks
    private MedicalRecordController medicalRecordController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private MedicalRecord medicalRecord;
    private MedicalRecordDTO medicalRecordDTO;
    private List<MedicalRecord> medicalRecordList;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(medicalRecordController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // FIX: Register JavaTimeModule for LocalDate support

        // Setup test data
        medicalRecord = new MedicalRecord();
        medicalRecord.setId(1L);
        medicalRecord.setDiagnosis("Common Cold");
        medicalRecord.setPrescription("Rest and hydration");
        medicalRecord.setNotes("Patient should rest for 3 days");

        medicalRecordDTO = new MedicalRecordDTO();
        medicalRecordDTO.setId(1L);
        medicalRecordDTO.setPatientId(1L);
        medicalRecordDTO.setMedicalStaffId(1L);
        medicalRecordDTO.setDiagnosis("Common Cold");
        medicalRecordDTO.setPrescription("Rest and hydration");
        medicalRecordDTO.setNotes("Patient should rest for 3 days");
        medicalRecordDTO.setRecordDate(LocalDate.now());

        MedicalRecord medicalRecord2 = new MedicalRecord();
        medicalRecord2.setId(2L);
        medicalRecord2.setDiagnosis("Flu");
        medicalRecord2.setPrescription("Antiviral medication");
        medicalRecord2.setNotes("Monitor temperature");

        medicalRecordList = Arrays.asList(medicalRecord, medicalRecord2);
    }

    @Test
    void getAllMedicalRecords_ShouldReturnListOfMedicalRecords() throws Exception {
        // Arrange
        when(medicalService.getAllMedicalRecords()).thenReturn(medicalRecordList);

        // Act & Assert
        mockMvc.perform(get("/api/medicalRecords"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));

        verify(medicalService, times(1)).getAllMedicalRecords();
    }

    @Test
    void getAllMedicalRecords_WhenEmpty_ShouldReturnEmptyList() throws Exception {
        // Arrange
        when(medicalService.getAllMedicalRecords()).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/medicalRecords"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(medicalService, times(1)).getAllMedicalRecords();
    }

    @Test
    void getMedicalRecordById_WhenExists_ShouldReturnMedicalRecord() throws Exception {
        // Arrange
        when(medicalService.getMedicalRecordById(1L)).thenReturn(Optional.of(medicalRecord));

        // Act & Assert
        mockMvc.perform(get("/api/medicalRecords/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.diagnosis").value("Common Cold"));

        verify(medicalService, times(1)).getMedicalRecordById(1L);
    }

    @Test
    void getMedicalRecordById_WhenNotExists_ShouldReturnNotFound() throws Exception {
        // Arrange
        when(medicalService.getMedicalRecordById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/medicalRecords/1"))
                .andExpect(status().isNotFound());

        verify(medicalService, times(1)).getMedicalRecordById(1L);
    }

    @Test
    void getMedicalRecordById_WhenServiceThrowsException_ShouldReturnInternalServerError() throws Exception {
        // Arrange
        when(medicalService.getMedicalRecordById(1L)).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        mockMvc.perform(get("/api/medicalRecords/1"))
                .andExpect(status().isInternalServerError());

        verify(medicalService, times(1)).getMedicalRecordById(1L);
    }

    @Test
    void getMedicalRecordByPatient_WhenRecordsExist_ShouldReturnMedicalRecords() throws Exception {
        // Arrange
        when(medicalService.getMedicalRecordByPatientId(1L)).thenReturn(medicalRecordList);

        // Act & Assert
        mockMvc.perform(get("/api/medicalRecords/by-patient/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));

        verify(medicalService, times(1)).getMedicalRecordByPatientId(1L);
    }

    @Test
    void getMedicalRecordByPatient_WhenNoRecords_ShouldReturnNotFound() throws Exception {
        // Arrange
        when(medicalService.getMedicalRecordByPatientId(1L)).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/medicalRecords/by-patient/1"))
                .andExpect(status().isNotFound());

        verify(medicalService, times(1)).getMedicalRecordByPatientId(1L);
    }

    @Test
    void createMedicalRecord_WithValidData_ShouldReturnCreated() throws Exception {
        // Arrange
        when(medicalService.createMedicalRecord(any(MedicalRecordDTO.class))).thenReturn(medicalRecordDTO);

        // Act & Assert
        mockMvc.perform(post("/api/medicalRecords")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(medicalRecordDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.diagnosis").value("Common Cold"));

        verify(medicalService, times(1)).createMedicalRecord(any(MedicalRecordDTO.class));
    }

    @Test
    void createMedicalRecord_WhenDuplicateResource_ShouldReturnBadRequest() throws Exception {
        // Arrange
        when(medicalService.createMedicalRecord(any(MedicalRecordDTO.class)))
                .thenThrow(new DuplicateResourceException("Duplicate record"));

        // Act & Assert
        mockMvc.perform(post("/api/medicalRecords")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(medicalRecordDTO)))
                .andExpect(status().isBadRequest());

        verify(medicalService, times(1)).createMedicalRecord(any(MedicalRecordDTO.class));
    }

    @Test
    void updateMedicalRecord_WithValidData_ShouldReturnUpdatedRecord() throws Exception {
        // Arrange
        when(medicalService.updateMedicalRecord(anyLong(), any(MedicalRecordDTO.class))).thenReturn(medicalRecordDTO);

        // Act & Assert
        mockMvc.perform(put("/api/medicalRecords/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(medicalRecordDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.diagnosis").value("Common Cold"));

        verify(medicalService, times(1)).updateMedicalRecord(anyLong(), any(MedicalRecordDTO.class));
    }

    @Test
    void updateMedicalRecord_WhenNotFound_ShouldReturnNotFound() throws Exception {
        // Arrange
        when(medicalService.updateMedicalRecord(anyLong(), any(MedicalRecordDTO.class)))
                .thenThrow(new ResourceNotFound("Medical record not found"));

        // Act & Assert
        mockMvc.perform(put("/api/medicalRecords/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(medicalRecordDTO)))
                .andExpect(status().isNotFound());

        verify(medicalService, times(1)).updateMedicalRecord(anyLong(), any(MedicalRecordDTO.class));
    }

    @Test
    void updateMedicalRecord_WhenIllegalArgument_ShouldReturnBadRequest() throws Exception {
        // Arrange
        when(medicalService.updateMedicalRecord(anyLong(), any(MedicalRecordDTO.class)))
                .thenThrow(new com.example.CommunityHealthMedicalSystem.Exception.IllegalArgumentException("Invalid data"));

        // Act & Assert
        mockMvc.perform(put("/api/medicalRecords/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(medicalRecordDTO)))
                .andExpect(status().isBadRequest());

        verify(medicalService, times(1)).updateMedicalRecord(anyLong(), any(MedicalRecordDTO.class));
    }

    @Test
    void updateMedicalRecord_WhenConflict_ShouldReturnConflict() throws Exception {
        // Arrange
        when(medicalService.updateMedicalRecord(anyLong(), any(MedicalRecordDTO.class)))
                .thenThrow(new ConflictException("Data conflict"));

        // Act & Assert
        mockMvc.perform(put("/api/medicalRecords/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(medicalRecordDTO)))
                .andExpect(status().isConflict());

        verify(medicalService, times(1)).updateMedicalRecord(anyLong(), any(MedicalRecordDTO.class));
    }

    @Test
    void deleteMedicalRecord_WithValidId_ShouldReturnNoContent() throws Exception {
        // Arrange
        doNothing().when(medicalService).deleteMedicalRecord(1L, 1L);

        // Act & Assert
        mockMvc.perform(delete("/api/medicalRecords/1")
                        .param("medicalStaffId", "1"))
                .andExpect(status().isNoContent());

        verify(medicalService, times(1)).deleteMedicalRecord(1L, 1L);
    }

    @Test
    void deleteMedicalRecord_WhenNotFound_ShouldReturnNotFound() throws Exception {
        // Arrange
        doThrow(new ResourceNotFound("Medical record not found"))
                .when(medicalService).deleteMedicalRecord(1L, 1L);

        // Act & Assert
        mockMvc.perform(delete("/api/medicalRecords/1")
                        .param("medicalStaffId", "1"))
                .andExpect(status().isNotFound());

        verify(medicalService, times(1)).deleteMedicalRecord(1L, 1L);
    }

    @Test
    void deleteMedicalRecord_WhenSecurityException_ShouldReturnForbidden() throws Exception {
        // Arrange
        doThrow(new SecurityException("Access denied"))
                .when(medicalService).deleteMedicalRecord(1L, 1L);

        // Act & Assert
        mockMvc.perform(delete("/api/medicalRecords/1")
                        .param("medicalStaffId", "1"))
                .andExpect(status().isForbidden());

        verify(medicalService, times(1)).deleteMedicalRecord(1L, 1L);
    }

    @Test
    void createMedicalRecord_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Arrange - Create a completely invalid DTO
        MedicalRecordDTO invalidDTO = new MedicalRecordDTO();
        // Don't set any required fields - this should trigger validation

        // Act & Assert
        mockMvc.perform(post("/api/medicalRecords")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(medicalService, never()).createMedicalRecord(any(MedicalRecordDTO.class));
    }

    // Additional test for validation in PUT requests
    @Test
    void updateMedicalRecord_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Arrange - Create an invalid DTO
        MedicalRecordDTO invalidDTO = new MedicalRecordDTO();
        // Don't set any required fields

        // Act & Assert
        mockMvc.perform(put("/api/medicalRecords/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(medicalService, never()).updateMedicalRecord(anyLong(), any(MedicalRecordDTO.class));
    }
}