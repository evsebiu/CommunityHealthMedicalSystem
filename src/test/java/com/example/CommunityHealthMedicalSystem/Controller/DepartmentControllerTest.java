package com.example.CommunityHealthMedicalSystem.Controller;

import com.example.CommunityHealthMedicalSystem.DTO.DepartmentDTO;
import com.example.CommunityHealthMedicalSystem.Exception.ConflictException;
import com.example.CommunityHealthMedicalSystem.Exception.DuplicateResourceException;
import com.example.CommunityHealthMedicalSystem.Exception.IllegalArgumentException;
import com.example.CommunityHealthMedicalSystem.Exception.ResourceNotFound;
import com.example.CommunityHealthMedicalSystem.Model.Department;
import com.example.CommunityHealthMedicalSystem.Repository.DepartmentRepository;
import com.example.CommunityHealthMedicalSystem.Service.DepartmentServiceImpl;
import com.example.CommunityHealthMedicalSystem.Service.MedicalStaffServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class DepartmentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private DepartmentServiceImpl departmentService;

    @Mock
    private MedicalStaffServiceImpl medicalStaffService;

    @Mock
    private DepartmentRepository departmentRepo;

    @InjectMocks
    private DepartmentController departmentController;

    private ObjectMapper objectMapper;
    private Department department;
    private DepartmentDTO departmentDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(departmentController).build();
        objectMapper = new ObjectMapper();

        department = new Department();
        department.setId(1L);
        department.setName("Cardiology");
        department.setDescription("Cardiology Department");

        departmentDTO = new DepartmentDTO();
        departmentDTO.setName("Cardiology");
        departmentDTO.setDescription("Cardiology Department");
    }

    // Test for GET /api/departments
    @Test
    void getAllDepartments_ShouldReturnListOfDepartments() throws Exception {
        List<Department> departments = Arrays.asList(department, new Department());
        when(departmentService.getAllDepartments()).thenReturn(departments);

        mockMvc.perform(get("/api/departments"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(departmentService, times(1)).getAllDepartments();
    }

    @Test
    void getAllDepartments_ShouldReturnEmptyList() throws Exception {
        when(departmentService.getAllDepartments()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/departments"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(departmentService, times(1)).getAllDepartments();
    }

    // Test for GET /api/departments/{id}
    @Test
    void getDepartmentById_WhenExists_ShouldReturnDepartment() throws Exception {
        when(departmentService.getDepartmentById(1L)).thenReturn(Optional.of(department));

        mockMvc.perform(get("/api/departments/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(departmentService, times(1)).getDepartmentById(1L);
    }

    @Test
    void getDepartmentById_WhenNotExists_ShouldReturnNotFound() throws Exception {
        when(departmentService.getDepartmentById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/departments/1"))
                .andExpect(status().isNotFound());

        verify(departmentService, times(1)).getDepartmentById(1L);
    }

    @Test
    void getDepartmentById_WhenException_ShouldReturnInternalServerError() throws Exception {
        when(departmentService.getDepartmentById(1L)).thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(get("/api/departments/1"))
                .andExpect(status().isInternalServerError());

        verify(departmentService, times(1)).getDepartmentById(1L);
    }

    // Test for GET /api/departments/name/{name}
    @Test
    void getDepartmentByNameIgnoreCase_WhenExists_ShouldReturnDepartment() throws Exception {
        when(departmentService.getDepartmentByNameIgnoreCase("Cardiology")).thenReturn(Optional.of(department));

        mockMvc.perform(get("/api/departments/name/Cardiology"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(departmentService, times(1)).getDepartmentByNameIgnoreCase("Cardiology");
    }

    @Test
    void getDepartmentByNameIgnoreCase_WhenNotExists_ShouldReturnNotFound() throws Exception {
        when(departmentService.getDepartmentByNameIgnoreCase("Unknown")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/departments/name/Unknown"))
                .andExpect(status().isNotFound());

        verify(departmentService, times(1)).getDepartmentByNameIgnoreCase("Unknown");
    }

    // Test for GET /api/departments/get-by-staff/{medicalStaffId}
    @Test
    void getDepartmentByMedicalStaff_WhenExists_ShouldReturnDepartments() throws Exception {
        List<Department> departments = Arrays.asList(department);
        when(departmentService.getDepartmentByMedicalStaff(1L)).thenReturn(departments);

        mockMvc.perform(get("/api/departments/get-by-staff/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(departmentService, times(1)).getDepartmentByMedicalStaff(1L);
    }

    @Test
    void getDepartmentByMedicalStaff_WhenNotExists_ShouldReturnNotFound() throws Exception {
        when(departmentService.getDepartmentByMedicalStaff(1L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/departments/get-by-staff/1"))
                .andExpect(status().isNotFound());

        verify(departmentService, times(1)).getDepartmentByMedicalStaff(1L);
    }

    // Test for GET /api/departments/search
    @Test
    void searchDepartmentByName_WithValidName_ShouldReturnDepartment() throws Exception {
        when(departmentRepo.findByNameIgnoreCase("Cardiology")).thenReturn(Optional.of(department));

        mockMvc.perform(get("/api/departments/search")
                        .param("name", "Cardiology"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(departmentRepo, times(1)).findByNameIgnoreCase("Cardiology");
    }

    @Test
    void searchDepartmentByName_WithNonExistentName_ShouldReturnNotFound() throws Exception {
        when(departmentRepo.findByNameIgnoreCase("Unknown")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/departments/search")
                        .param("name", "Unknown"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Department not found with nameUnknown"));

        verify(departmentRepo, times(1)).findByNameIgnoreCase("Unknown");
    }

    @Test
    void searchDepartmentByName_WithoutName_ShouldReturnAllDepartments() throws Exception {
        List<Department> departments = Arrays.asList(department);
        when(departmentRepo.findAll()).thenReturn(departments);

        mockMvc.perform(get("/api/departments/search"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(departmentRepo, times(1)).findAll();
    }

    @Test
    void searchDepartmentByName_WithoutNameAndNoDepartments_ShouldReturnNotFound() throws Exception {
        when(departmentRepo.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/departments/search"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("No departments found in system."));

        verify(departmentRepo, times(1)).findAll();
    }

    @Test
    void searchDepartmentByName_WhenException_ShouldReturnInternalServerError() throws Exception {
        when(departmentRepo.findByNameIgnoreCase(anyString())).thenThrow(new RuntimeException("Search error"));

        mockMvc.perform(get("/api/departments/search")
                        .param("name", "Cardiology"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Search failed"));

        verify(departmentRepo, times(1)).findByNameIgnoreCase("Cardiology");
    }

    // Test for POST /api/departments
    @Test
    void createDepartment_WithValidData_ShouldReturnCreated() throws Exception {
        when(departmentService.createDepartment(any(DepartmentDTO.class))).thenReturn(departmentDTO);

        mockMvc.perform(post("/api/departments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(departmentDTO)))
                .andExpect(status().isCreated());

        verify(departmentService, times(1)).createDepartment(any(DepartmentDTO.class));
    }

    @Test
    void createDepartment_WithDuplicateResource_ShouldReturnBadRequest() throws Exception {
        when(departmentService.createDepartment(any(DepartmentDTO.class)))
                .thenThrow(new DuplicateResourceException("Department already exists"));

        mockMvc.perform(post("/api/departments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(departmentDTO)))
                .andExpect(status().isBadRequest());

        verify(departmentService, times(1)).createDepartment(any(DepartmentDTO.class));
    }

    // Test for PATCH /api/departments/{id}/name
    @Test
    void updateDepartmentName_WithValidData_ShouldReturnOk() throws Exception {
        DepartmentDTO updatedDTO = new DepartmentDTO();
        updatedDTO.setName("New Cardiology");
        // For PATCH, description might not be required since we're only updating name
        when(departmentService.updateDepartment(anyLong(), any(DepartmentDTO.class))).thenReturn(updatedDTO);

        mockMvc.perform(patch("/api/departments/1/name")
                        .param("name", "New Cardiology"))
                .andExpect(status().isOk());

        verify(departmentService, times(1)).updateDepartment(anyLong(), any(DepartmentDTO.class));
    }

    @Test
    void updateDepartmentName_WhenDepartmentNotFound_ShouldReturnNotFound() throws Exception {
        when(departmentService.updateDepartment(anyLong(), any(DepartmentDTO.class)))
                .thenThrow(new ResourceNotFound("Department not found"));

        mockMvc.perform(patch("/api/departments/1/name")
                        .param("name", "New Cardiology"))
                .andExpect(status().isNotFound());

        verify(departmentService, times(1)).updateDepartment(anyLong(), any(DepartmentDTO.class));
    }

    @Test
    void updateDepartmentName_WithIllegalArgument_ShouldReturnBadRequest() throws Exception {
        when(departmentService.updateDepartment(anyLong(), any(DepartmentDTO.class)))
                .thenThrow(new IllegalArgumentException("Invalid name"));

        mockMvc.perform(patch("/api/departments/1/name")
                        .param("name", ""))
                .andExpect(status().isBadRequest());

        verify(departmentService, times(1)).updateDepartment(anyLong(), any(DepartmentDTO.class));
    }

    // Test for PUT /api/departments/{id}
    @Test
    void updateDepartment_WithValidData_ShouldReturnOk() throws Exception {
        // Make sure the DTO has all required fields for validation
        DepartmentDTO validDTO = new DepartmentDTO();
        validDTO.setName("Updated Cardiology");
        validDTO.setDescription("Updated Cardiology Department Description");

        when(departmentService.updateDepartment(anyLong(), any(DepartmentDTO.class))).thenReturn(validDTO);

        mockMvc.perform(put("/api/departments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDTO)))
                .andExpect(status().isOk());

        verify(departmentService, times(1)).updateDepartment(anyLong(), any(DepartmentDTO.class));
    }

    @Test
    void updateDepartment_WhenDepartmentNotFound_ShouldReturnNotFound() throws Exception {
        DepartmentDTO validDTO = new DepartmentDTO();
        validDTO.setName("Updated Cardiology");
        validDTO.setDescription("Updated Cardiology Department Description");

        when(departmentService.updateDepartment(anyLong(), any(DepartmentDTO.class)))
                .thenThrow(new ResourceNotFound("Department not found"));

        mockMvc.perform(put("/api/departments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDTO)))
                .andExpect(status().isNotFound());

        verify(departmentService, times(1)).updateDepartment(anyLong(), any(DepartmentDTO.class));
    }

    @Test
    void updateDepartment_WithIllegalArgument_ShouldReturnBadRequest() throws Exception {
        DepartmentDTO validDTO = new DepartmentDTO();
        validDTO.setName("Updated Cardiology");
        validDTO.setDescription("Updated Cardiology Department Description");

        when(departmentService.updateDepartment(anyLong(), any(DepartmentDTO.class)))
                .thenThrow(new IllegalArgumentException("Invalid data"));

        mockMvc.perform(put("/api/departments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDTO)))
                .andExpect(status().isBadRequest());

        verify(departmentService, times(1)).updateDepartment(anyLong(), any(DepartmentDTO.class));
    }

    @Test
    void updateDepartment_WithConflict_ShouldReturnConflict() throws Exception {
        DepartmentDTO validDTO = new DepartmentDTO();
        validDTO.setName("Updated Cardiology");
        validDTO.setDescription("Updated Cardiology Department Description");

        when(departmentService.updateDepartment(anyLong(), any(DepartmentDTO.class)))
                .thenThrow(new ConflictException("Conflict occurred"));

        mockMvc.perform(put("/api/departments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDTO)))
                .andExpect(status().isConflict());

        verify(departmentService, times(1)).updateDepartment(anyLong(), any(DepartmentDTO.class));
    }

    @Test
    void updateDepartment_WithInvalidDTO_ShouldReturnBadRequest() throws Exception {
        // Create an invalid DTO without required fields
        DepartmentDTO invalidDTO = new DepartmentDTO();
        invalidDTO.setName("Cardiology");
        // Missing description field which is required

        mockMvc.perform(put("/api/departments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        // Verify that the service method was never called due to validation failure
        verify(departmentService, never()).updateDepartment(anyLong(), any(DepartmentDTO.class));
    }

    // Test for DELETE /api/departments/{id}
    @Test
    void deleteDepartment_WithValidId_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/departments/1"))
                .andExpect(status().isNoContent());

        verify(departmentService, times(1)).deleteDepartment(1L);
    }

    @Test
    void deleteDepartment_WhenDepartmentNotFound_ShouldReturnNotFound() throws Exception {
        doThrow(new ResourceNotFound("Department not found")).when(departmentService).deleteDepartment(1L);

        mockMvc.perform(delete("/api/departments/1"))
                .andExpect(status().isNotFound());

        verify(departmentService, times(1)).deleteDepartment(1L);
    }

    @Test
    void deleteDepartment_WithIllegalArgument_ShouldReturnBadRequest() throws Exception {
        doThrow(new IllegalArgumentException("Invalid id")).when(departmentService).deleteDepartment(1L);

        mockMvc.perform(delete("/api/departments/1"))
                .andExpect(status().isBadRequest());

        verify(departmentService, times(1)).deleteDepartment(1L);
    }

    @Test
    void deleteDepartment_WhenGeneralException_ShouldReturnInternalServerError() throws Exception {
        doThrow(new RuntimeException("Database error")).when(departmentService).deleteDepartment(1L);

        mockMvc.perform(delete("/api/departments/1"))
                .andExpect(status().isInternalServerError());

        verify(departmentService, times(1)).deleteDepartment(1L);
    }
}