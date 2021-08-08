package com.github.wandpsilva.grpc.calculator.server;

import com.proto.calculator.*;
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
}
