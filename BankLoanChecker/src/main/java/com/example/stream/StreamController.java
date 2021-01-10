/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Adnan
 */
@RestController
public class StreamController {
    
    private static final int MAX_AMOUNT = 1000;
    
    private LoanChecker checker;

    public StreamController(LoanChecker binding) {
        checker = binding;
    }

    @StreamListener(target = "orderChannel")
    public void processLoan(Loan loan) {
        if (loan.getLoan_amount() <= MAX_AMOUNT) {
            Message<Loan> checker = MessageBuilder.withPayload(loan).build();
            this.checker.approved().send(checker);
            Message<String> result = MessageBuilder.withPayload(loan.getName() + " is approved").build();
            this.checker.status().send(result);
        }
        else {
            Message<Loan> checker = MessageBuilder.withPayload(loan).build();
            this.checker.declined().send(checker);
            Message<String> result = MessageBuilder.withPayload(loan.getName() + " is declined").build();
            this.checker.status().send(result);
        }
    }
    
}

interface LoanChecker {
    
    String ORDER_IN = "orderChannel";
    String APPROVED_OUT = "approved";
    String DECLINED_OUT = "declined";
    String STATUS_OUT = "statusChannel";

    @Input(ORDER_IN)
    SubscribableChannel order();

    @Output(APPROVED_OUT)
    MessageChannel approved();

    @Output(DECLINED_OUT)
    MessageChannel declined();
    
    @Output(STATUS_OUT)
    MessageChannel status();

}