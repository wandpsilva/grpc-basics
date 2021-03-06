package com.github.wandpsilva.grpc.calculator.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.protobuf.services.ProtoReflectionService;

import java.io.IOException;

public class CalculatorServer {

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("gRPC server calculator");

        Server server = ServerBuilder.forPort(50051)
                .addService(new CalculatorServiceImpl())
                .addService(ProtoReflectionService.newInstance()) // for reflection
                .build();
        server.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Received shutdown request");
            server.shutdown();
            System.out.println("Succesfully stopped server");
        }));

        server.awaitTermination();
    }
}
