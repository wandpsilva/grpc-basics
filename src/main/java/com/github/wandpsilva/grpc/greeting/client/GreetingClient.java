package com.github.wandpsilva.grpc.greeting.client;

import com.proto.greet.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class GreetingClient {

    ManagedChannel channel;

    public static void main(String[] args) {
        GreetingClient main = new GreetingClient();
        main.run();
    }

    private void run() {
        System.out.println("gRPC client");

        channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        //doUnaryCall(channel);
        //doServersStreaming(channel);

        doClientStreamingCall(channel);

        System.out.println("shutting down gRPC client");
        channel.shutdown();
    }

    private void doClientStreamingCall(ManagedChannel channel) {
        GreetServiceGrpc.GreetServiceStub asyncClient = GreetServiceGrpc.newStub(channel);

        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<LongGreetRequest> requestObserver = asyncClient.longGreet(new StreamObserver<LongGreetResponse>() {
            @Override
            public void onNext(LongGreetResponse value) {
                System.out.println("Received a response from the server!");
                System.out.println(value.getResult());
            }

            @Override
            public void onError(Throwable t) {
                //nothing here
            }

            @Override
            public void onCompleted() {
                System.out.println("Server has completed sending us something!");
                latch.countDown();
            }
        });
        requestObserver.onNext(LongGreetRequest.newBuilder()
                .setGreeting(Greeting.newBuilder()
                        .setFirstName("Skarlet")
                        .build())
                .build());
        requestObserver.onNext(LongGreetRequest.newBuilder()
                .setGreeting(Greeting.newBuilder()
                        .setFirstName("Smoke")
                        .build())
                .build());
        requestObserver.onNext(LongGreetRequest.newBuilder()
                .setGreeting(Greeting.newBuilder()
                        .setFirstName("Scorpion")
                        .build())
                .build());

        requestObserver.onCompleted();

        try {
            latch.await(3L, TimeUnit.SECONDS);
        }
        catch (InterruptedException ex) {
            ex.printStackTrace();
        }

    }

    private void doServersStreaming(ManagedChannel channel) {
        //creating a greet service client (blockingstub for synchronous)
        GreetServiceGrpc.GreetServiceBlockingStub greetClient = GreetServiceGrpc.newBlockingStub(channel);

        GreetManyTimesRequest greetManyTimesRequest = GreetManyTimesRequest.newBuilder()
                .setGreeting(Greeting.newBuilder().setFirstName("Wander"))
                .build();

        greetClient.greetManyTimes(greetManyTimesRequest).forEachRemaining(greetManyTimesResponse -> {
            System.out.println(greetManyTimesResponse.getResult());
        });
    }

    private void doUnaryCall(ManagedChannel channel) {
        //creating a greet service client (blockingstub for synchronous)
        GreetServiceGrpc.GreetServiceBlockingStub greetClient = GreetServiceGrpc.newBlockingStub(channel);

        //creating protobuf greeting message
        Greeting greeting = Greeting.newBuilder()
                .setFirstName("Wander")
                .setLastName("P da Silva")
                .build();

        //creating protobuf greeting message
        GreetRequest greetRequest = GreetRequest.newBuilder()
                .setGreeting(greeting)
                .build();

        //creating protobuf greeting response
        GreetResponse greetResponse = greetClient.greet(greetRequest);

        System.out.println(greetResponse.getResult());

    }

}
