package com.github.wandpsilva.grpc.calculator.client;

import com.proto.calculator.*;
import com.proto.greet.GreetRequest;
import com.proto.greet.GreetResponse;
import com.proto.greet.GreetServiceGrpc;
import com.proto.greet.Greeting;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class CalculatorClient {

    public static void main(String[] args) {
        System.out.println("gRPC client");

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051).usePlaintext().build();

        System.out.println("creating a stub");

        //creating a greet service client (blockingstub for synchronous)
        CalculatorServiceGrpc.CalculatorServiceBlockingStub calculatorClient = CalculatorServiceGrpc.newBlockingStub(channel);

//        // UNARY
//        CalculatorRequest request = CalculatorRequest.newBuilder()
//                .setFirstNumber(50)
//                .setSecondNumber(10)
//                .build();
//
//        CalculatorMessage calculatorMessage = CalculatorMessage.newBuilder()
//                .setCalculatorRequest(request)
//                .build();
//
//        CalculatorResponse response = calculatorClient.calculator(calculatorMessage);
//
//        System.out.println(response.getResult());

        //SERVER STREAMING
        Long number = 564565489798798797L;
        calculatorClient.primeNumberDecomposition(PrimeNumberDecompositionRequest.newBuilder()
                .setNumber(number)
                .build())
                .forEachRemaining(primeNumberDecompositionResponse -> {
                    System.out.println(primeNumberDecompositionResponse.getPrimeFactor());
                });

        System.out.println("shutting down gRPC client");
        channel.shutdown();
    }
}
