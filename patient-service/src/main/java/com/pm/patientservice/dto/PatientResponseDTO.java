package com.pm.patientservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientResponseDTO {
    private UUID id;
    private String name;
    private String email;
    private String address;
    private String dateOfBirth;
    private String registeredDate;
}