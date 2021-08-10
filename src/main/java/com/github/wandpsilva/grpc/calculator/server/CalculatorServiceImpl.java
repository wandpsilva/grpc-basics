package com.github.wandpsilva.grpc.calculator.server;

import com.proto.calculator.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

public class CalculatorServiceImpl extends CalculatorServiceGrpc.CalculatorServiceImplBase {

    @Override
    public void calculator(CalculatorMessage request, StreamObserver<CalculatorResponse> responseObserver) {
        //get the fields we need
        CalculatorRequest calculatorRequest = request.getCalculatorRequest();
        int n1 = calculatorRequest.getFirstNumber();
        int n2 = calculatorRequest.getSecondNumber();

        //create the response
        String result = "Result = " + (n1 + n2);
        CalculatorResponse response = CalculatorResponse.newBuilder().setResult(result).build();

        //send the response
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void primeNumberDecomposition(PrimeNumberDecompositionRequest request, StreamObserver<PrimeNumberDecompositionResponse> responseObserver) {
        Long number = request.getNumber();
        Integer divisor = 2;
        while (number > 1) {
            if(number % divisor == 0) {
                number = number / divisor;

                responseObserver.onNext(PrimeNumberDecompositionResponse.newBuilder()
                        .setPrimeFactor(number)
                        .build());
            } else {
                divisor = divisor + 1;
            }
        }
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<ComputeAverageRequest> computeAverage(StreamObserver<ComputeAverageResponse> responseObserver) {
        StreamObserver<ComputeAverageRequest> requestObserver = new StreamObserver<ComputeAverageRequest>() {

            int sum = 0;
            int count = 0;

            @Override
            public void onNext(ComputeAverageRequest value) {
                sum = sum + value.getNumber();
                count = count + 1;
            }

            @Override
            public void onError(Throwable t) {
                //nothing here for while
            }

            @Override
            public void onCompleted() {
                double avg = (double) sum / count;
                responseObserver.onNext(ComputeAverageResponse.newBuilder()
                        .setAverage(avg)
                        .build());
                responseObserver.onCompleted();
            }
        };
        return requestObserver;
    }

    @Override
    public void squareRoot(SquareRootRequest request, StreamObserver<SquareRootResponse> responseObserver) {
        Integer number = request.getNumber();

        if(number > 0) {
            double numberRoot = Math.sqrt(number);
            responseObserver.onNext(SquareRootResponse.newBuilder()
                    .setNumberRoot(numberRoot)
                    .build());
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("The number being sent is no positive")
                    .augmentDescription("number sent: " + number)
                    .asRuntimeException());
        }
    }
}
