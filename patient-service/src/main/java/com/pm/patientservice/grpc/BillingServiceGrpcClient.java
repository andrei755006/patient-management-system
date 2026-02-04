package com.pm.patientservice.grpc;

import com.pm.billingservice.grpc.billing.BillingResponse;
import com.pm.billingservice.grpc.billing.BillingRequest;
import com.pm.billingservice.grpc.billing.BillingServiceGrpc;
import com.pm.patientservice.model.Patient; // Importing Patient domain model
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List; // Used for patient roles

@Service
@Slf4j
public class BillingServiceGrpcClient {

    private final BillingServiceGrpc.BillingServiceBlockingStub blockingStub;

    public BillingServiceGrpcClient(
            @Value("${billing.service.address:localhost}") String serverAddress,
            @Value("${billing.service.grpc.port:9001}") int serverPort) {

        log.info("Connecting to Billing Service gRPC at {}:{}", serverAddress, serverPort);

        ManagedChannel channel = ManagedChannelBuilder.forAddress(serverAddress, serverPort)
                .usePlaintext()
                .build();

        blockingStub = BillingServiceGrpc.newBlockingStub(channel);
    }

    // UPDATED SIGNATURE: Accepts a Patient object and a list of roles
    public BillingResponse createBillingAccount(Patient patient, List<String> roles) {
        log.info("Sending gRPC request for patient: {} with roles: {}", patient.getId(), roles);

        // Using 'var' for cleaner and more modern code
        var request = BillingRequest.newBuilder()
                .setPatientId(patient.getId().toString())
                .setName(patient.getName())
                .setEmail(patient.getEmail())
                .addAllRoles(roles) // This field was added in the .proto definition
                .build();

        try {
            var response = blockingStub.createBillingAccount(request);
            log.info("Billing account created. ID: {}, Status: {}", response.getAccountId(), response.getStatus());
            return response;
        } catch (Exception e) {
            log.error("gRPC call failed: {}", e.getMessage());
            throw e;
        }
    }
}
