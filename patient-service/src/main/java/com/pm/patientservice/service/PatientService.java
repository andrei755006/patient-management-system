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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor // Generates constructor for all final fields
public class PatientService {

    private final PatientRepository patientRepository;
    private final BillingServiceGrpcClient billingServiceGrpcClient;
    private final KafkaProducer kafkaProducer;
    private final PatientMapper patientMapper; // Now injected as a bean

    public List<PatientResponseDTO> getPatients() {
        log.info("Retrieving all patients from database");
        return patientRepository.findAll().stream()
                .map(patientMapper::toDTO)
                .toList();
    }

    @Transactional // Ensures database, gRPC, and Kafka stay consistent
    public PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO) {
        if (patientRepository.existsByEmail(patientRequestDTO.getEmail())) {
            throw new EmailAlreadyExistsException("A patient with this email already exists: " + patientRequestDTO.getEmail());
        }

        // 1. Map DTO to Entity and Save
        Patient patient = patientMapper.toModel(patientRequestDTO);
        Patient savedPatient = patientRepository.save(patient);
        log.info("Saved new patient with ID: {}", savedPatient.getId());

        // 2. Call Billing Service via gRPC
        try {
            billingServiceGrpcClient.createBillingAccount(
                    savedPatient.getId().toString(),
                    savedPatient.getName(),
                    savedPatient.getEmail()
            );
        } catch (Exception e) {
            log.error("Failed to create billing account for patient {}: {}", savedPatient.getId(), e.getMessage());
            // Optionally throw exception to rollback transaction
        }

        // 3. Send event to Kafka
        kafkaProducer.sendEvent(savedPatient);

        return patientMapper.toDTO(savedPatient);
    }

    @Transactional
    public PatientResponseDTO updatePatient(UUID id, PatientRequestDTO patientRequestDTO) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found with id: " + id));

        if (patientRepository.existsByEmailAndIdNot(patientRequestDTO.getEmail(), id)) {
            throw new EmailAlreadyExistsException("Email already in use by another patient: " + patientRequestDTO.getEmail());
        }

        // Use MapStruct to update existing entity with DTO data
        // For this to work, we'll add one method to PatientMapper (see below)
        patientMapper.updatePatientFromDto(patientRequestDTO, patient);

        Patient updatedPatient = patientRepository.save(patient);
        log.info("Updated patient with ID: {}", id);
        return patientMapper.toDTO(updatedPatient);
    }

    public void deletePatient(UUID id) {
        log.warn("Deleting patient with ID: {}", id);
        patientRepository.deleteById(id);
    }
}