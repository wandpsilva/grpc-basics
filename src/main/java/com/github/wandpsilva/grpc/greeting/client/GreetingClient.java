package com.github.wandpsilva.grpc.greeting.client;

import com.proto.dummy.DummyServiceGrpc;
import com.proto.greet.GreetRequest;
import com.proto.greet.GreetResponse;
import com.proto.greet.GreetServiceGrpc;
import com.proto.greet.Greeting;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GreetingClient {

    public static void main(String[] args) {
        System.out.println("gRPC client");

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051).usePlaintext().build();

        System.out.println("creating a stub");

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

        System.out.println("shutting down gRPC client");
        channel.shutdown();
    }
}
