package com.pm.patientservice.grpc;

// Эти импорты должны совпасть с тем, что я написал в .proto (Вариант А)
import com.pm.billingservice.grpc.billing.BillingResponse;
import com.pm.billingservice.grpc.billing.BillingRequest;
import com.pm.billingservice.grpc.billing.BillingServiceGrpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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

    public BillingResponse createBillingAccount(String patientId, String name, String email) {
        log.info("Sending gRPC request to create billing account for patient: {}", patientId);

        BillingRequest request = BillingRequest.newBuilder()
                .setPatientId(patientId)
                .setName(name)
                .setEmail(email)
                .build();

        try {
            BillingResponse response = blockingStub.createBillingAccount(request);
            log.info("Billing account created. ID: {}, Status: {}", response.getAccountId(), response.getStatus());
            return response;
        } catch (Exception e) {
            log.error("gRPC call failed: {}", e.getMessage());
            throw e;
        }
    }
}