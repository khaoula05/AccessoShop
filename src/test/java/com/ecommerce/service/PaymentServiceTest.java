package com.ecommerce.service;

import com.ecommerce.entity.Payment;
import com.ecommerce.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository repo;

    @InjectMocks
    private PaymentService service;

    @Test
    void shouldProcessPayment() {

        Payment payment = new Payment();

        when(repo.save(any(Payment.class)))
                .thenReturn(payment);

        Payment result = service.pay(payment);

        assertNotNull(result);
        assertEquals("PAYE", payment.getStatus());

        verify(repo).save(payment);
    }
}