package com.github.wandpsilva.grpc.calculator.client;

import com.proto.calculator.*;
import com.proto.greet.GreetRequest;
import com.proto.greet.GreetResponse;
import com.proto.greet.GreetServiceGrpc;
import com.proto.greet.Greeting;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class CalculatorClient {

    public static void main(String[] args) {
        CalculatorClient main = new CalculatorClient();
        main.run();

        System.out.println("creating a stub");
    }

    private void run() {
        System.out.println("gRPC client");
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        //doUnaryCall(channel);
        //doServerStreamingCall(channel);
        doClientStreamingCall(channel);

        System.out.println("shutting down gRPC client");
        channel.shutdown();
    }

    private void doServerStreamingCall(ManagedChannel channel) {
        CalculatorServiceGrpc.CalculatorServiceBlockingStub calculatorClient = CalculatorServiceGrpc.newBlockingStub(channel);

        Long number = 564565489798798797L;
        calculatorClient.primeNumberDecomposition(PrimeNumberDecompositionRequest.newBuilder()
                .setNumber(number)
                .build())
                .forEachRemaining(primeNumberDecompositionResponse -> {
                    System.out.println(primeNumberDecompositionResponse.getPrimeFactor());
                });
    }

    private void doUnaryCall(ManagedChannel channel) {
        CalculatorServiceGrpc.CalculatorServiceBlockingStub calculatorClient = CalculatorServiceGrpc.newBlockingStub(channel);

        CalculatorRequest request = CalculatorRequest.newBuilder()
                .setFirstNumber(50)
                .setSecondNumber(10)
                .build();

        CalculatorMessage calculatorMessage = CalculatorMessage.newBuilder()
                .setCalculatorRequest(request)
                .build();

        CalculatorResponse response = calculatorClient.calculator(calculatorMessage);

        System.out.println(response.getResult());
    }

    private void doClientStreamingCall(ManagedChannel channel) {
        CalculatorServiceGrpc.CalculatorServiceStub asyncClient = CalculatorServiceGrpc.newStub(channel);

        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<ComputeAverageRequest> requestOserver = asyncClient.computeAverage(new StreamObserver<ComputeAverageResponse>() {
            @Override
            public void onNext(ComputeAverageResponse value) {
                System.out.println("Received the response from the server");
                System.out.println(value.getAverage());
            }

            @Override
            public void onError(Throwable t) {
                //nothing for while
            }

            @Override
            public void onCompleted() {
                System.out.println("Server has completed sending us data!");
                latch.countDown();
            }
        });
        requestOserver.onNext(ComputeAverageRequest.newBuilder()
                .setNumber(1)
                .build());
        requestOserver.onNext(ComputeAverageRequest.newBuilder()
                .setNumber(2)
                .build());
        requestOserver.onNext(ComputeAverageRequest.newBuilder()
                .setNumber(3)
                .build());
        requestOserver.onCompleted();

        try {
            latch.await(3, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}
