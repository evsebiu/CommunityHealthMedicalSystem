package com.example.CommunityHealthMedicalSystem.Controller;

import com.example.CommunityHealthMedicalSystem.DTO.MedicalStaffDTO;
import com.example.CommunityHealthMedicalSystem.Exception.ConflictException;
import com.example.CommunityHealthMedicalSystem.Exception.DuplicateResourceException;
import com.example.CommunityHealthMedicalSystem.Exception.ResourceNotFound;
import com.example.CommunityHealthMedicalSystem.Model.MedicalStaff;
import com.example.CommunityHealthMedicalSystem.Service.MedicalStaffServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

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
public class MedicalStaffControllerTest {

    private MockMvc mockMvc;

    @Mock
    private MedicalStaffServiceImpl medicalStaffService;

    @InjectMocks
    private MedicalStaffController medicalStaffController;

    private ObjectMapper objectMapper;
    private MedicalStaff medicalStaff;
    private MedicalStaffDTO medicalStaffDTO;
    private List<MedicalStaff> medicalStaffList;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(medicalStaffController).build();
        objectMapper = new ObjectMapper();

        // Setup test data
        medicalStaff = new MedicalStaff();
        medicalStaff.setId(1L);
        medicalStaff.setFirstName("John");
        medicalStaff.setLastName("Doe");
        medicalStaff.setEmail("john.doe@hospital.com");
        medicalStaff.setLicenseNumber("MED12345");
        medicalStaff.setSpecialization("Cardiology");
        medicalStaff.setRole(MedicalStaff.Role.DOCTOR);

        medicalStaffDTO = new MedicalStaffDTO();
        medicalStaffDTO.setId(1L);
        medicalStaffDTO.setFirstName("John");
        medicalStaffDTO.setLastName("Doe");
        medicalStaffDTO.setEmail("john.doe@hospital.com");
        medicalStaffDTO.setLicenseNumber("MED12345");
        medicalStaffDTO.setSpecialization("Cardiology");
        medicalStaffDTO.setRole(MedicalStaff.Role.DOCTOR);
        medicalStaffDTO.setDepartmentId(1L);

        MedicalStaff medicalStaff2 = new MedicalStaff();
        medicalStaff2.setId(2L);
        medicalStaff2.setFirstName("Jane");
        medicalStaff2.setLastName("Smith");
        medicalStaff2.setEmail("jane.smith@hospital.com");
        medicalStaff2.setLicenseNumber("MED67890");
        medicalStaff2.setSpecialization("Neurology");
        medicalStaff2.setRole(MedicalStaff.Role.NURSE);

        medicalStaffList = Arrays.asList(medicalStaff, medicalStaff2);
    }

    // Test for GET /api/medicalStaff
    @Test
    void getAllMedicalStaff_ShouldReturnListOfMedicalStaff() throws Exception {
        when(medicalStaffService.getAllMedicalStaff()).thenReturn(medicalStaffList);

        mockMvc.perform(get("/api/medicalStaff"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].firstName").value("Jane"));

        verify(medicalStaffService, times(1)).getAllMedicalStaff();
    }

    @Test
    void getAllMedicalStaff_WhenEmpty_ShouldReturnEmptyList() throws Exception {
        when(medicalStaffService.getAllMedicalStaff()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/medicalStaff"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(medicalStaffService, times(1)).getAllMedicalStaff();
    }

    // Test for GET /api/medicalStaff/{medicalStaffId}
    @Test
    void getMedicalStaffById_WhenExists_ShouldReturnMedicalStaff() throws Exception {
        when(medicalStaffService.getMedicalStaffById(1L)).thenReturn(Optional.of(medicalStaff));

        mockMvc.perform(get("/api/medicalStaff/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@hospital.com"));

        verify(medicalStaffService, times(1)).getMedicalStaffById(1L);
    }

    @Test
    void getMedicalStaffById_WhenNotExists_ShouldReturnNotFound() throws Exception {
        when(medicalStaffService.getMedicalStaffById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/medicalStaff/1"))
                .andExpect(status().isNotFound());

        verify(medicalStaffService, times(1)).getMedicalStaffById(1L);
    }

    @Test
    void getMedicalStaffById_WhenServiceThrowsResourceNotFound_ShouldReturnBadRequest() throws Exception {
        when(medicalStaffService.getMedicalStaffById(1L)).thenThrow(new ResourceNotFound("Medical staff not found"));

        mockMvc.perform(get("/api/medicalStaff/1"))
                .andExpect(status().isBadRequest());

        verify(medicalStaffService, times(1)).getMedicalStaffById(1L);
    }

    // Test for GET /api/medicalStaff/by-department/{departmentId}
    @Test
    void getMedicalStaffByDepartmentId_WhenStaffExists_ShouldReturnStaffList() throws Exception {
        when(medicalStaffService.getByDepartment(1L)).thenReturn(medicalStaffList);

        mockMvc.perform(get("/api/medicalStaff/by-department/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));

        verify(medicalStaffService, times(1)).getByDepartment(1L);
    }

    @Test
    void getMedicalStaffByDepartmentId_WhenNoStaff_ShouldReturnNotFound() throws Exception {
        when(medicalStaffService.getByDepartment(1L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/medicalStaff/by-department/1"))
                .andExpect(status().isNotFound());

        verify(medicalStaffService, times(1)).getByDepartment(1L);
    }

    // Test for POST /api/medicalStaff
    @Test
    void createMedicalStaff_WithValidData_ShouldReturnCreated() throws Exception {
        when(medicalStaffService.createStaff(any(MedicalStaffDTO.class))).thenReturn(medicalStaffDTO);

        mockMvc.perform(post("/api/medicalStaff")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(medicalStaffDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@hospital.com"));

        verify(medicalStaffService, times(1)).createStaff(any(MedicalStaffDTO.class));
    }

    @Test
    void createMedicalStaff_WhenDuplicateResource_ShouldReturnBadRequest() throws Exception {
        when(medicalStaffService.createStaff(any(MedicalStaffDTO.class)))
                .thenThrow(new DuplicateResourceException("Duplicate medical staff"));

        mockMvc.perform(post("/api/medicalStaff")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(medicalStaffDTO)))
                .andExpect(status().isBadRequest());

        verify(medicalStaffService, times(1)).createStaff(any(MedicalStaffDTO.class));
    }

    @Test
    void createMedicalStaff_WhenResourceNotFound_ShouldReturnNotFound() throws Exception {
        when(medicalStaffService.createStaff(any(MedicalStaffDTO.class)))
                .thenThrow(new ResourceNotFound("Department not found"));

        mockMvc.perform(post("/api/medicalStaff")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(medicalStaffDTO)))
                .andExpect(status().isNotFound());

        verify(medicalStaffService, times(1)).createStaff(any(MedicalStaffDTO.class));
    }

    @Test
    void createMedicalStaff_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        MedicalStaffDTO invalidDTO = new MedicalStaffDTO();
        // Leave all required fields empty/null to trigger validation errors
        invalidDTO.setFirstName("");
        invalidDTO.setLastName("");
        invalidDTO.setEmail("invalid-email");
        invalidDTO.setLicenseNumber("");
        invalidDTO.setSpecialization("");
        invalidDTO.setRole(null);
        invalidDTO.setDepartmentId(null);

        mockMvc.perform(post("/api/medicalStaff")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(medicalStaffService, never()).createStaff(any(MedicalStaffDTO.class));
    }

    // Test for PUT /api/medicalStaff/{id}
    @Test
    void updateMedicalStaff_WithValidData_ShouldReturnUpdatedStaff() throws Exception {
        when(medicalStaffService.updateStaff(eq(1L), any(MedicalStaffDTO.class))).thenReturn(medicalStaffDTO);

        mockMvc.perform(put("/api/medicalStaff/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(medicalStaffDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));

        verify(medicalStaffService, times(1)).updateStaff(eq(1L), any(MedicalStaffDTO.class));
    }

    @Test
    void updateMedicalStaff_WhenResourceNotFound_ShouldReturnNotFound() throws Exception {
        when(medicalStaffService.updateStaff(eq(1L), any(MedicalStaffDTO.class)))
                .thenThrow(new ResourceNotFound("Medical staff not found"));

        mockMvc.perform(put("/api/medicalStaff/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(medicalStaffDTO)))
                .andExpect(status().isNotFound());

        verify(medicalStaffService, times(1)).updateStaff(eq(1L), any(MedicalStaffDTO.class));
    }

    @Test
    void updateMedicalStaff_WhenIllegalArgument_ShouldReturnBadRequest() throws Exception {
        when(medicalStaffService.updateStaff(eq(1L), any(MedicalStaffDTO.class)))
                .thenThrow(new com.example.CommunityHealthMedicalSystem.Exception.IllegalArgumentException("Invalid argument"));

        mockMvc.perform(put("/api/medicalStaff/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(medicalStaffDTO)))
                .andExpect(status().isBadRequest());

        verify(medicalStaffService, times(1)).updateStaff(eq(1L), any(MedicalStaffDTO.class));
    }

    @Test
    void updateMedicalStaff_WhenConflict_ShouldReturnConflict() throws Exception {
        when(medicalStaffService.updateStaff(eq(1L), any(MedicalStaffDTO.class)))
                .thenThrow(new ConflictException("Conflict occurred"));

        mockMvc.perform(put("/api/medicalStaff/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(medicalStaffDTO)))
                .andExpect(status().isConflict());

        verify(medicalStaffService, times(1)).updateStaff(eq(1L), any(MedicalStaffDTO.class));
    }

    @Test
    void updateMedicalStaff_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        MedicalStaffDTO invalidDTO = new MedicalStaffDTO();
        // Leave all required fields empty/null to trigger validation errors
        invalidDTO.setFirstName("");
        invalidDTO.setLastName("");
        invalidDTO.setEmail("invalid-email");
        invalidDTO.setLicenseNumber("");
        invalidDTO.setSpecialization("");
        invalidDTO.setRole(null);
        invalidDTO.setDepartmentId(null);

        mockMvc.perform(put("/api/medicalStaff/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(medicalStaffService, never()).updateStaff(anyLong(), any(MedicalStaffDTO.class));
    }

    // Test for DELETE /api/medicalStaff/{id}
    @Test
    void deleteMedicalStaff_WhenExists_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/medicalStaff/1"))
                .andExpect(status().isNoContent());

        verify(medicalStaffService, times(1)).deleteStaff(1L);
    }

    @Test
    void deleteMedicalStaff_WhenResourceNotFound_ShouldReturnNotFound() throws Exception {
        doThrow(new ResourceNotFound("Medical staff not found")).when(medicalStaffService).deleteStaff(1L);

        mockMvc.perform(delete("/api/medicalStaff/1"))
                .andExpect(status().isNotFound());

        verify(medicalStaffService, times(1)).deleteStaff(1L);
    }

    @Test
    void deleteMedicalStaff_WhenSecurityException_ShouldReturnForbidden() throws Exception {
        doThrow(new SecurityException("Access denied")).when(medicalStaffService).deleteStaff(1L);

        mockMvc.perform(delete("/api/medicalStaff/1"))
                .andExpect(status().isForbidden());

        verify(medicalStaffService, times(1)).deleteStaff(1L);
    }
}