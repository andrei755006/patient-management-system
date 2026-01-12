package com.pm.billingservice.grpc;

// Импорты изменятся после правки .proto
import com.pm.billingservice.grpc.billing.BillingRequest;
import com.pm.billingservice.grpc.billing.BillingResponse;
import com.pm.billingservice.grpc.billing.BillingServiceGrpc.BillingServiceImplBase;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import lombok.extern.slf4j.Slf4j;

@GrpcService
@Slf4j // Заменяем ручной логгер на аннотацию
public class BillingGrpcService extends BillingServiceImplBase {

    @Override
    public void createBillingAccount(BillingRequest billingRequest,
                                     StreamObserver<BillingResponse> responseObserver) {

        log.info("Creating billing account for patient: {}, email: {}",
                billingRequest.getPatientId(), billingRequest.getEmail());

        // Твоя бизнес-логика
        BillingResponse response = BillingResponse.newBuilder()
                .setAccountId("ACC-" + java.util.UUID.randomUUID().toString().substring(0, 8))
                .setStatus("ACTIVE")
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
        log.info("Billing account created successfully");
    }
}