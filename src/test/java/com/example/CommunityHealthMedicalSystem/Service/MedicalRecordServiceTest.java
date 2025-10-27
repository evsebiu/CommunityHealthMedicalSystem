package com.example.CommunityHealthMedicalSystem.Service;

import com.example.CommunityHealthMedicalSystem.DTO.MedicalRecordDTO;
import com.example.CommunityHealthMedicalSystem.Exception.DuplicateResourceException;
import com.example.CommunityHealthMedicalSystem.Exception.IllegalArgumentException;
import com.example.CommunityHealthMedicalSystem.Exception.ResourceNotFound;
import com.example.CommunityHealthMedicalSystem.Exception.SecurityException;
import com.example.CommunityHealthMedicalSystem.Model.MedicalRecord;
import com.example.CommunityHealthMedicalSystem.Model.MedicalStaff;
import com.example.CommunityHealthMedicalSystem.Model.Patient;
import com.example.CommunityHealthMedicalSystem.Repository.MedicalRecordRepository;
import com.example.CommunityHealthMedicalSystem.Repository.MedicalStaffRepository;
import com.example.CommunityHealthMedicalSystem.Repository.PatientRepository;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MedicalRecordServiceTest {

    @Mock
    private MedicalRecordRepository medicalRecordRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private MedicalStaffRepository medicalStaffRepository;

    @InjectMocks
    private MedicalRecordServiceImpl medicalRecordService;

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

    private MedicalRecord createSampleMedicalRecord() {
        MedicalRecord record = new MedicalRecord();
        record.setId(1L);
        record.setPatient(createSamplePatient());
        record.setMedicalStaff(createSampleMedicalStaff());
        record.setDiagnosis("Common Cold");
        record.setPrescription("Rest and fluids");
        record.setNotes("Patient shows mild symptoms");
        record.setRecordDate(LocalDate.of(2024, 1, 15));
        return record;
    }

    private MedicalRecordDTO createSampleMedicalRecordDTO() {
        MedicalRecordDTO dto = new MedicalRecordDTO();
        dto.setId(1L);
        dto.setPatientId(1L);
        dto.setMedicalStaffId(1L);
        dto.setDiagnosis("Common Cold");
        dto.setPrescription("Rest and fluids");
        dto.setNotes("Patient shows mild symptoms");
        dto.setRecordDate(LocalDate.of(2024, 1, 15));
        return dto;
    }

    // Test for getAllMedicalRecords()
    @Test
    void getAllMedicalRecords_ShouldReturnAllRecords() {
        // Given
        MedicalRecord record1 = createSampleMedicalRecord();
        MedicalRecord record2 = createSampleMedicalRecord();
        record2.setId(2L);

        List<MedicalRecord> expectedRecords = Arrays.asList(record1, record2);
        when(medicalRecordRepository.findAll()).thenReturn(expectedRecords);

        // When
        List<MedicalRecord> result = medicalRecordService.getAllMedicalRecords();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(medicalRecordRepository, times(1)).findAll();
    }

    // Test for getMedicalRecordById()
    @Test
    void getMedicalRecordById_WithValidId_ShouldReturnRecord() {
        // Given
        Long recordId = 1L;
        MedicalRecord expectedRecord = createSampleMedicalRecord();
        when(medicalRecordRepository.findById(recordId)).thenReturn(Optional.of(expectedRecord));

        // When
        Optional<MedicalRecord> result = medicalRecordService.getMedicalRecordById(recordId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(expectedRecord, result.get());
        verify(medicalRecordRepository, times(1)).findById(recordId);
    }

    @Test
    void getMedicalRecordById_WithNullId_ShouldThrowException() {
        // Given & When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> medicalRecordService.getMedicalRecordById(null));

        assertEquals("Medical record ID cannot be null!", exception.getMessage());
        verify(medicalRecordRepository, never()).findById(anyLong());
    }

    @Test
    void getMedicalRecordById_WithNonExistentId_ShouldReturnEmpty() {
        // Given
        Long recordId = 999L;
        when(medicalRecordRepository.findById(recordId)).thenReturn(Optional.empty());

        // When
        Optional<MedicalRecord> result = medicalRecordService.getMedicalRecordById(recordId);

        // Then
        assertFalse(result.isPresent());
        verify(medicalRecordRepository, times(1)).findById(recordId);
    }

    // Test for findByDiagnosisIgnoreCase()
    @Test
    void findByDiagnosisIgnoreCase_WithValidDiagnosis_ShouldReturnRecords() {
        // Given
        String diagnosis = "Common Cold";
        List<MedicalRecord> expectedRecords = Arrays.asList(createSampleMedicalRecord());
        when(medicalRecordRepository.findByDiagnosisIgnoreCase(diagnosis)).thenReturn(expectedRecords);

        // When
        List<MedicalRecord> result = medicalRecordService.findByDiagnosisIgnoreCase(diagnosis);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(medicalRecordRepository, times(1)).findByDiagnosisIgnoreCase(diagnosis);
    }

    @Test
    void findByDiagnosisIgnoreCase_WithNullDiagnosis_ShouldThrowException() {
        // Given & When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> medicalRecordService.findByDiagnosisIgnoreCase(null));

        assertEquals("Patient diagnosis cannot be null!", exception.getMessage());
        verify(medicalRecordRepository, never()).findByDiagnosisIgnoreCase(any());
    }

    // Test for findByPrescriptionIgnoreCase()
    @Test
    void findByPrescriptionIgnoreCase_WithValidPrescription_ShouldReturnRecords() {
        // Given
        String prescription = "Rest and fluids";
        List<MedicalRecord> expectedRecords = Arrays.asList(createSampleMedicalRecord());
        when(medicalRecordRepository.findByPrescription(prescription)).thenReturn(expectedRecords);

        // When
        List<MedicalRecord> result = medicalRecordService.findByPrescriptionIgnoreCase(prescription);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(medicalRecordRepository, times(1)).findByPrescription(prescription);
    }

    @Test
    void findByPrescriptionIgnoreCase_WithNullPrescription_ShouldThrowException() {
        // Given & When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> medicalRecordService.findByPrescriptionIgnoreCase(null));

        assertEquals("Patient prescription cannot be null!", exception.getMessage());
        verify(medicalRecordRepository, never()).findByPrescription(any());
    }

    // Test for findByNotesIgnoreCase()
    @Test
    void findByNotesIgnoreCase_WithValidNotes_ShouldReturnRecords() {
        // Given
        String notes = "Patient shows mild symptoms";
        List<MedicalRecord> expectedRecords = Arrays.asList(createSampleMedicalRecord());
        when(medicalRecordRepository.findByNotes(notes)).thenReturn(expectedRecords);

        // When
        List<MedicalRecord> result = medicalRecordService.findByNotesIgnoreCase(notes);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(medicalRecordRepository, times(1)).findByNotes(notes);
    }

    @Test
    void findByNotesIgnoreCase_WithNullNotes_ShouldThrowException() {
        // Given & When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> medicalRecordService.findByNotesIgnoreCase(null));

        assertEquals("Patient notes are required!", exception.getMessage());
        verify(medicalRecordRepository, never()).findByNotes(any());
    }

    // Test for findByRecordDate()
    @Test
    void findByRecordDate_WithValidDate_ShouldReturnRecords() {
        // Given
        LocalDate recordDate = LocalDate.of(2024, 1, 15);
        List<MedicalRecord> expectedRecords = Arrays.asList(createSampleMedicalRecord());
        when(medicalRecordRepository.findByRecordDate(recordDate)).thenReturn(expectedRecords);

        // When
        List<MedicalRecord> result = medicalRecordService.findByRecordDate(recordDate);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(medicalRecordRepository, times(1)).findByRecordDate(recordDate);
    }

    @Test
    void findByRecordDate_WithNullDate_ShouldThrowException() {
        // Given & When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> medicalRecordService.findByRecordDate(null));

        assertEquals("Record date is required, it cannot be null.", exception.getMessage());
        verify(medicalRecordRepository, never()).findByRecordDate(any());
    }

    // Test for getMedicalRecordByMedicalStaff()
    @Test
    void getMedicalRecordByMedicalStaff_WithValidStaff_ShouldReturnRecords() {
        // Given
        MedicalStaff medicalStaff = createSampleMedicalStaff();
        List<MedicalRecord> expectedRecords = Arrays.asList(createSampleMedicalRecord());
        when(medicalRecordRepository.findByMedicalStaff(medicalStaff)).thenReturn(expectedRecords);

        // When
        List<MedicalRecord> result = medicalRecordService.getMedicalRecordByMedicalStaff(medicalStaff);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(medicalRecordRepository, times(1)).findByMedicalStaff(medicalStaff);
    }

    @Test
    void getMedicalRecordByMedicalStaff_WithNullStaff_ShouldThrowException() {
        // Given & When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> medicalRecordService.getMedicalRecordByMedicalStaff(null));

        assertEquals("Medical staff cannot be null, it's required.", exception.getMessage());
        verify(medicalRecordRepository, never()).findByMedicalStaff(any());
    }

    // Test for getMedicalRecordByDateRange()
    @Test
    void getMedicalRecordByDateRange_WithValidDates_ShouldReturnRecords() {
        // Given
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);
        List<MedicalRecord> expectedRecords = Arrays.asList(createSampleMedicalRecord());
        when(medicalRecordRepository.findByRecordDateBetween(startDate, endDate)).thenReturn(expectedRecords);

        // When
        List<MedicalRecord> result = medicalRecordService.getMedicalRecordByDateRange(startDate, endDate);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(medicalRecordRepository, times(1)).findByRecordDateBetween(startDate, endDate);
    }

    @Test
    void getMedicalRecordByDateRange_WithNullStartDate_ShouldThrowException() {
        // Given & When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> medicalRecordService.getMedicalRecordByDateRange(null, LocalDate.now()));

        assertEquals("Start date of medical record cannot be null.", exception.getMessage());
        verify(medicalRecordRepository, never()).findByRecordDateBetween(any(), any());
    }

    @Test
    void getMedicalRecordByDateRange_WithNullEndDate_ShouldThrowException() {
        // Given & When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> medicalRecordService.getMedicalRecordByDateRange(LocalDate.now(), null));

        assertEquals("End date of medical record cannot be null", exception.getMessage());
        verify(medicalRecordRepository, never()).findByRecordDateBetween(any(), any());
    }

    @Test
    void getMedicalRecordByDateRange_WithStartDateAfterEndDate_ShouldThrowException() {
        // Given
        LocalDate startDate = LocalDate.of(2024, 1, 31);
        LocalDate endDate = LocalDate.of(2024, 1, 1);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> medicalRecordService.getMedicalRecordByDateRange(startDate, endDate));

        assertEquals("Start date cannot be afte the end date.", exception.getMessage());
        verify(medicalRecordRepository, never()).findByRecordDateBetween(any(), any());
    }

    @Test
    void getMedicalRecordByDateRange_WithFutureStartDate_ShouldThrowException() {
        // Given
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(2);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> medicalRecordService.getMedicalRecordByDateRange(startDate, endDate));

        assertEquals("Dates cannot be in future", exception.getMessage());
        verify(medicalRecordRepository, never()).findByRecordDateBetween(any(), any());
    }

    // Test for getMedicalRecordByPatient()
    @Test
    void getMedicalRecordByPatient_WithValidPatient_ShouldReturnRecords() {
        // Given
        Patient patient = createSamplePatient();
        List<MedicalRecord> expectedRecords = Arrays.asList(createSampleMedicalRecord());
        when(medicalRecordRepository.findMedicalRecordByPatient(patient)).thenReturn(expectedRecords);

        // When
        List<MedicalRecord> result = medicalRecordService.getMedicalRecordByPatient(patient);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(medicalRecordRepository, times(1)).findMedicalRecordByPatient(patient);
    }

    @Test
    void getMedicalRecordByPatient_WithNullPatient_ShouldThrowException() {
        // Given & When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> medicalRecordService.getMedicalRecordByPatient(null));

        assertEquals("Patient field is requierd.", exception.getMessage());
        verify(medicalRecordRepository, never()).findMedicalRecordByPatient(any());
    }

    // Test for getMedicalRecordByPatientId()
    @Test
    void getMedicalRecordByPatientId_WithValidPatientId_ShouldReturnRecords() {
        // Given
        Long patientId = 1L;
        List<MedicalRecord> expectedRecords = Arrays.asList(createSampleMedicalRecord());
        when(medicalRecordRepository.findMedicalRecordByPatientId(patientId)).thenReturn(expectedRecords);

        // When
        List<MedicalRecord> result = medicalRecordService.getMedicalRecordByPatientId(patientId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(medicalRecordRepository, times(1)).findMedicalRecordByPatientId(patientId);
    }

    @Test
    void getMedicalRecordByPatientId_WithNullPatientId_ShouldThrowException() {
        // Given & When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> medicalRecordService.getMedicalRecordByPatientId(null));

        assertEquals("Patiend ID is required to get Medical Records.", exception.getMessage());
        verify(medicalRecordRepository, never()).findMedicalRecordByPatientId(anyLong());
    }

    // Test for createMedicalRecord()
    @Test
    void createMedicalRecord_WithValidData_ShouldReturnMedicalRecordDTO() {
        // Given
        MedicalRecordDTO inputDTO = createSampleMedicalRecordDTO();
        Patient patient = createSamplePatient();
        MedicalStaff medicalStaff = createSampleMedicalStaff();
        MedicalRecord savedRecord = createSampleMedicalRecord();

        when(patientRepository.findById(inputDTO.getPatientId())).thenReturn(Optional.of(patient));
        when(medicalStaffRepository.findById(inputDTO.getMedicalStaffId())).thenReturn(Optional.of(medicalStaff));
        when(medicalRecordRepository.existsByPatientAndRecordDate(patient, inputDTO.getRecordDate())).thenReturn(false);
        when(medicalRecordRepository.save(any(MedicalRecord.class))).thenReturn(savedRecord);

        // When
        MedicalRecordDTO result = medicalRecordService.createMedicalRecord(inputDTO);

        // Then
        assertNotNull(result);
        assertEquals(savedRecord.getId(), result.getId());
        assertEquals(savedRecord.getDiagnosis(), result.getDiagnosis());
        verify(patientRepository, times(1)).findById(inputDTO.getPatientId());
        verify(medicalStaffRepository, times(1)).findById(inputDTO.getMedicalStaffId());
        verify(medicalRecordRepository, times(1)).existsByPatientAndRecordDate(patient, inputDTO.getRecordDate());
        verify(medicalRecordRepository, times(1)).save(any(MedicalRecord.class));
    }

    @Test
    void createMedicalRecord_WithNullDTO_ShouldThrowException() {
        // Given & When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> medicalRecordService.createMedicalRecord(null));

        assertEquals("Medical record is required, it cannot be null.", exception.getMessage());
        verify(medicalRecordRepository, never()).save(any(MedicalRecord.class));
    }

    @Test
    void createMedicalRecord_WithNullPatientId_ShouldThrowException() {
        // Given
        MedicalRecordDTO inputDTO = createSampleMedicalRecordDTO();
        inputDTO.setPatientId(null);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> medicalRecordService.createMedicalRecord(inputDTO));

        assertEquals("Patient field is required, it cannot be null.", exception.getMessage());
        verify(medicalRecordRepository, never()).save(any(MedicalRecord.class));
    }

    @Test
    void createMedicalRecord_WithNullMedicalStaffId_ShouldThrowException() {
        // Given
        MedicalRecordDTO inputDTO = createSampleMedicalRecordDTO();
        inputDTO.setMedicalStaffId(null);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> medicalRecordService.createMedicalRecord(inputDTO));

        assertEquals("Medical staff is a required field, it cannot be null.", exception.getMessage());
        verify(medicalRecordRepository, never()).save(any(MedicalRecord.class));
    }

    @Test
    void createMedicalRecord_WithNonExistentPatient_ShouldThrowException() {
        // Given
        MedicalRecordDTO inputDTO = createSampleMedicalRecordDTO();
        when(patientRepository.findById(inputDTO.getPatientId())).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFound exception = assertThrows(ResourceNotFound.class,
                () -> medicalRecordService.createMedicalRecord(inputDTO));

        assertEquals("Patient with ID " + inputDTO.getPatientId() + " does not exists in database.", exception.getMessage());
        verify(medicalRecordRepository, never()).save(any(MedicalRecord.class));
    }

    @Test
    void createMedicalRecord_WithNonExistentMedicalStaff_ShouldThrowException() {
        // Given
        MedicalRecordDTO inputDTO = createSampleMedicalRecordDTO();
        Patient patient = createSamplePatient();
        when(patientRepository.findById(inputDTO.getPatientId())).thenReturn(Optional.of(patient));
        when(medicalStaffRepository.findById(inputDTO.getMedicalStaffId())).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFound exception = assertThrows(ResourceNotFound.class,
                () -> medicalRecordService.createMedicalRecord(inputDTO));

        assertEquals("Medical staff with ID" + inputDTO.getMedicalStaffId() + " does not exists in database.", exception.getMessage());
        verify(medicalRecordRepository, never()).save(any(MedicalRecord.class));
    }

    @Test
    void createMedicalRecord_WithDuplicateRecord_ShouldThrowException() {
        // Given
        MedicalRecordDTO inputDTO = createSampleMedicalRecordDTO();
        Patient patient = createSamplePatient();
        MedicalStaff medicalStaff = createSampleMedicalStaff();

        when(patientRepository.findById(inputDTO.getPatientId())).thenReturn(Optional.of(patient));
        when(medicalStaffRepository.findById(inputDTO.getMedicalStaffId())).thenReturn(Optional.of(medicalStaff));
        when(medicalRecordRepository.existsByPatientAndRecordDate(patient, inputDTO.getRecordDate())).thenReturn(true);

        // When & Then
        DuplicateResourceException exception = assertThrows(DuplicateResourceException.class,
                () -> medicalRecordService.createMedicalRecord(inputDTO));

        assertEquals("Medical record with same details already exists.", exception.getMessage());
        verify(medicalRecordRepository, never()).save(any(MedicalRecord.class));
    }

    // Test for deleteMedicalRecord()
    @Test
    void deleteMedicalRecord_WithValidData_ShouldDeleteRecord() {
        // Given
        Long recordId = 1L;
        Long medicalStaffId = 1L;
        MedicalRecord existingRecord = createSampleMedicalRecord();
        when(medicalRecordRepository.findById(recordId)).thenReturn(Optional.of(existingRecord));

        // When
        medicalRecordService.deleteMedicalRecord(recordId, medicalStaffId);

        // Then
        verify(medicalRecordRepository, times(1)).findById(recordId);
        verify(medicalRecordRepository, times(1)).delete(existingRecord);
    }

    @Test
    void deleteMedicalRecord_WithNullMedicalStaffId_ShouldThrowException() {
        // Given
        Long recordId = 1L;

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> medicalRecordService.deleteMedicalRecord(recordId, null));

        assertEquals("Deleting staff cannot be null. Field is required.", exception.getMessage());
        verify(medicalRecordRepository, never()).findById(anyLong());
        verify(medicalRecordRepository, never()).delete(any(MedicalRecord.class));
    }

    @Test
    void deleteMedicalRecord_WithNonExistentRecord_ShouldThrowException() {
        // Given
        Long recordId = 999L;
        Long medicalStaffId = 1L;
        when(medicalRecordRepository.findById(recordId)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> medicalRecordService.deleteMedicalRecord(recordId, medicalStaffId));

        assertEquals("Medical Record with id: " + recordId + " not found.", exception.getMessage());
        verify(medicalRecordRepository, never()).delete(any(MedicalRecord.class));
    }

    @Test
    void deleteMedicalRecord_WithUnauthorizedStaff_ShouldThrowException() {
        // Given
        Long recordId = 1L;
        Long unauthorizedStaffId = 2L; // Different from record's staff ID
        MedicalRecord existingRecord = createSampleMedicalRecord();
        when(medicalRecordRepository.findById(recordId)).thenReturn(Optional.of(existingRecord));

        // When & Then
        SecurityException exception = assertThrows(SecurityException.class,
                () -> medicalRecordService.deleteMedicalRecord(recordId, unauthorizedStaffId));

        assertEquals("Only authorised medical staff can do this operation", exception.getMessage());
        verify(medicalRecordRepository, never()).delete(any(MedicalRecord.class));
    }

    // Test for updateMedicalRecord()
    // Test for updateMedicalRecord()
    @Test
    void updateMedicalRecord_WithValidData_ShouldReturnUpdatedDTO() {
        // Given
        Long recordId = 1L;
        MedicalRecordDTO updateDTO = createSampleMedicalRecordDTO();
        updateDTO.setId(recordId);
        updateDTO.setDiagnosis("Updated Diagnosis");

        MedicalRecord existingRecord = createSampleMedicalRecord();
        Patient patient = createSamplePatient();
        MedicalStaff medicalStaff = createSampleMedicalStaff();

        when(medicalRecordRepository.findById(recordId)).thenReturn(Optional.of(existingRecord));
        when(patientRepository.findById(updateDTO.getPatientId())).thenReturn(Optional.of(patient));
        when(medicalStaffRepository.findById(updateDTO.getMedicalStaffId())).thenReturn(Optional.of(medicalStaff));
        when(medicalRecordRepository.save(any(MedicalRecord.class))).thenReturn(existingRecord);

        // When
        MedicalRecordDTO result = medicalRecordService.updateMedicalRecord(recordId, updateDTO);

        // Then
        assertNotNull(result);
        verify(medicalRecordRepository, times(1)).findById(recordId);
        verify(medicalRecordRepository, times(1)).save(existingRecord);
    }

    @Test
    void updateMedicalRecord_WithNonExistentRecord_ShouldThrowException() {
        // Given
        Long recordId = 999L;
        MedicalRecordDTO updateDTO = createSampleMedicalRecordDTO();
        updateDTO.setId(recordId);
        when(medicalRecordRepository.findById(recordId)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFound exception = assertThrows(ResourceNotFound.class,
                () -> medicalRecordService.updateMedicalRecord(recordId, updateDTO));

        assertEquals("Medical record with ID " + recordId + " does not exist in database.", exception.getMessage());
        verify(medicalRecordRepository, never()).save(any(MedicalRecord.class));
    }

    @Test
    void updateMedicalRecord_WithNullDTO_ShouldThrowException() {
        // Given
        Long recordId = 1L;
        MedicalRecord existingRecord = createSampleMedicalRecord();
        when(medicalRecordRepository.findById(recordId)).thenReturn(Optional.of(existingRecord));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> medicalRecordService.updateMedicalRecord(recordId, null));

        assertEquals("Medical record cannot be null.", exception.getMessage());
        verify(medicalRecordRepository, never()).save(any(MedicalRecord.class));
    }

    @Test
    void updateMedicalRecord_WithNewPatient_ShouldUpdatePatient() {
        // Given
        Long recordId = 1L;
        Long newPatientId = 2L;
        MedicalRecordDTO updateDTO = createSampleMedicalRecordDTO();
        updateDTO.setId(recordId);
        updateDTO.setPatientId(newPatientId);

        MedicalRecord existingRecord = createSampleMedicalRecord();
        Patient newPatient = createSamplePatient();
        newPatient.setId(newPatientId);
        MedicalStaff medicalStaff = createSampleMedicalStaff();

        when(medicalRecordRepository.findById(recordId)).thenReturn(Optional.of(existingRecord));
        when(patientRepository.findById(newPatientId)).thenReturn(Optional.of(newPatient));
        when(medicalStaffRepository.findById(updateDTO.getMedicalStaffId())).thenReturn(Optional.of(medicalStaff));
        when(medicalRecordRepository.save(any(MedicalRecord.class))).thenReturn(existingRecord);

        // When
        MedicalRecordDTO result = medicalRecordService.updateMedicalRecord(recordId, updateDTO);

        // Then
        assertNotNull(result);
        verify(patientRepository, times(1)).findById(newPatientId);
        verify(medicalRecordRepository, times(1)).save(existingRecord);
    }

    // Test for convertToDTO()
    @Test
    void convertToDTO_WithValidMedicalRecord_ShouldReturnCorrectDTO() {
        // Given
        MedicalRecord medicalRecord = createSampleMedicalRecord();

        // When
        MedicalRecordDTO result = medicalRecordService.convertToDTO(medicalRecord);

        // Then
        assertNotNull(result);
        assertEquals(medicalRecord.getId(), result.getId());
        assertEquals(medicalRecord.getDiagnosis(), result.getDiagnosis());
        assertEquals(medicalRecord.getPrescription(), result.getPrescription());
        assertEquals(medicalRecord.getNotes(), result.getNotes());
        assertEquals(medicalRecord.getRecordDate(), result.getRecordDate());
        assertEquals(medicalRecord.getPatient().getId(), result.getPatientId());
        assertEquals(medicalRecord.getMedicalStaff().getId(), result.getMedicalStaffId());
    }
}