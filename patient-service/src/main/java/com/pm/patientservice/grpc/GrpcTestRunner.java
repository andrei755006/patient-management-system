package com.pm.patientservice.grpc;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class GrpcTestRunner {

    private final BillingServiceGrpcClient billingClient;

    public GrpcTestRunner(BillingServiceGrpcClient billingClient) {
        this.billingClient = billingClient;
    }

    @Bean
    CommandLineRunner testGrpcCall() {
        return args -> {
            System.out.println("=== Starting gRPC test ===");
            try {
                var response = billingClient.createBillingAccount(
                        "12333",
                        "John Doe",
                        "john.doe@example.com"
                );
                System.out.println("gRPC Response: " + response);
            } catch (Exception e) {
                System.err.println("gRPC call failed: " + e.getMessage());
                e.printStackTrace();
            }
            System.out.println("=== gRPC test finished ===");
        };
    }
}
