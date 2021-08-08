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

        //creating protobuf greeting message
        CalculatorRequest request = CalculatorRequest.newBuilder()
                .setFirstNumber(50)
                .setSecondNumber(10)
                .build();

        //creating protobuf greeting message
        CalculatorMessage calculatorMessage = CalculatorMessage.newBuilder()
                .setCalculatorRequest(request)
                .build();

        //creating protobuf greeting response
        CalculatorResponse response = calculatorClient.calculator(calculatorMessage);

        System.out.println(response.getResult());

        System.out.println("shutting down gRPC client");
        channel.shutdown();
    }
}
