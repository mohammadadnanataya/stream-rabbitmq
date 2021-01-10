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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

/**
 *
 * @author Adnan
 */
@Controller
public class StreamController {

    private LoanOrder order;

    public StreamController(LoanOrder binding) {
        order = binding;
    }

    @GetMapping("/order")
    public String orderForm(Model model) {
        model.addAttribute("loan", new Loan());
        return "order";
    }

    @PostMapping("/order")
    public String orderSubmit(@ModelAttribute Loan loan, Model model) {
        model.addAttribute("loan", loan);
        Message<Loan> order = MessageBuilder.withPayload(loan).build();
        this.order.order().send(order);
        return "result";
    }
    
    @StreamListener(target = "statusChannel")
    public void loanStatus(String status) {
        System.out.println("Loan Status: " + status);
    }
    
}

interface LoanOrder {
    
    String ORDER_OUT = "orderChannel";
    String STATUS_IN = "statusChannel";
    
    @Output(ORDER_OUT)
    MessageChannel order();
    
    @Input(STATUS_IN)
    SubscribableChannel status();
    
}
