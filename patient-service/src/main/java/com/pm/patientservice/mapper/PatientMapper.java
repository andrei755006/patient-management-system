package com.pm.patientservice.mapper;

import java.time.LocalDate;

import com.pm.patientservice.dto.PatientResponseDTO;
import com.pm.patientservice.model.Patient;

import com.pm.patientservice.dto.PatientRequestDTO;


public class PatientMapper {
    public static PatientResponseDTO toDTO(Patient patient) {
        PatientResponseDTO PatientDto = new PatientResponseDTO();
        PatientDto.setId(patient.getId().toString());
        PatientDto.setName(patient.getName());
        PatientDto.setEmail(patient.getEmail());
        PatientDto.setAddress(patient.getAddress());
        PatientDto.setDateOfBirth(patient.getDateOfBirth().toString());
        
        return PatientDto;
    }

    public static Patient toModel(PatientRequestDTO patientRequestDTO) {
        Patient patient = new Patient();
        patient.setName(patientRequestDTO.getName());
        patient.setEmail(patientRequestDTO.getEmail());
        patient.setAddress(patientRequestDTO.getAddress());
        patient.setDateOfBirth(LocalDate.parse(patientRequestDTO.getDateOfBirth()));
        patient.setRegisteredDate(LocalDate.parse(patientRequestDTO.getRegisteredDate()));
        
        return patient;
    }
}
