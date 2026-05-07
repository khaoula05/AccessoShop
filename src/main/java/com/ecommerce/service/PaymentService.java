package com.ecommerce.service;

import com.ecommerce.entity.Payment;
import com.ecommerce.repository.PaymentRepository;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    private final PaymentRepository repo;

    public PaymentService(PaymentRepository repo) {
        this.repo = repo;
    }

    public Payment pay(Payment payment) {
        payment.setStatus("PAYE");
        return repo.save(payment);
    }
}
