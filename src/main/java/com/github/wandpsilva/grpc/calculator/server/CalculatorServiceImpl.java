package com.github.wandpsilva.grpc.calculator.server;

import com.proto.calculator.CalculatorMessage;
import com.proto.calculator.CalculatorRequest;
import com.proto.calculator.CalculatorResponse;
import com.proto.calculator.CalculatorServiceGrpc;
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
}
