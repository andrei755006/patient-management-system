package com.pm.patientservice.service;

import com.pm.patientservice.dto.PatientRequestDTO;
import com.pm.patientservice.dto.PatientResponseDTO;
import com.pm.patientservice.exception.EmailAlreadyExistsException;
import com.pm.patientservice.exception.PatientNotFoundException;
import com.pm.patientservice.grpc.BillingServiceGrpcClient;
import com.pm.patientservice.kafka.KafkaProducer;
import com.pm.patientservice.mapper.PatientMapper;
import com.pm.patientservice.model.Patient;
import com.pm.patientservice.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;
    private final BillingServiceGrpcClient billingServiceGrpcClient;
    private final KafkaProducer kafkaProducer;
    private final PatientMapper patientMapper;

    public List<PatientResponseDTO> getPatients() {
        log.info("Retrieving all patients from database");
        return patientRepository.findAll().stream()
                .map(patientMapper::toDTO)
                .toList();
    }

    @Transactional
    public PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO) {
        if (patientRepository.existsByEmail(patientRequestDTO.getEmail())) {
            throw new EmailAlreadyExistsException("A patient with this email already exists: " + patientRequestDTO.getEmail());
        }

        // Extract roles from the security context (from JWT)
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("Current user authorities: {}", authentication != null ? authentication.getAuthorities() : "NULL");
        List<String> roles = (authentication != null)
                ? authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList()
                : List.of();

        // 1. Map DTO to Entity and Save
        Patient patient = patientMapper.toModel(patientRequestDTO);
        Patient savedPatient = patientRepository.save(patient);
        log.info("Saved new patient with ID: {}", savedPatient.getId());

        // 2. Call Billing Service via gRPC
        try {
            billingServiceGrpcClient.createBillingAccount(savedPatient, roles);
        } catch (Exception e) {
            log.error("Failed to create billing account for patient {}: {}", savedPatient.getId(), e.getMessage());
        }

        // 3. Send event to Kafka
        kafkaProducer.sendEvent(savedPatient, "PATIENT_CREATED", roles);

        return patientMapper.toDTO(savedPatient);
    }

    @Transactional
    public PatientResponseDTO updatePatient(UUID id, PatientRequestDTO patientRequestDTO) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found with id: " + id));

        if (patientRepository.existsByEmailAndIdNot(patientRequestDTO.getEmail(), id)) {
            throw new EmailAlreadyExistsException("Email already in use by another patient: " + patientRequestDTO.getEmail());
        }

        // Fetch roles for update event
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        List<String> roles = (authentication != null)
                ? authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList()
                : List.of();

        patientMapper.updatePatientFromDto(patientRequestDTO, patient);
        Patient updatedPatient = patientRepository.save(patient);
        log.info("Updated patient with ID: {}", id);

        // Notify Kafka about the update
        kafkaProducer.sendEvent(updatedPatient, "PATIENT_UPDATED", roles);

        return patientMapper.toDTO(updatedPatient);
    }

    public void deletePatient(UUID id) {
        log.warn("Deleting patient with ID: {}", id);
        patientRepository.deleteById(id);
    }
}