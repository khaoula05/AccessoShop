package com.ecommerce.controller;

import com.ecommerce.entity.Payment;
import com.ecommerce.service.PaymentService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    private final PaymentService service;

    public PaymentController(PaymentService service) {
        this.service = service;
    }

    @PostMapping
    public Payment pay(@RequestBody Payment payment) {
        return service.pay(payment);
    }
}
