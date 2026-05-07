package com.ecommerce.service;

import com.ecommerce.entity.Order;
import com.ecommerce.repository.OrderRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository repo;

    public OrderService(OrderRepository repo) {
        this.repo = repo;
    }

    public Order createOrder(Order order) {
        return repo.save(order);
    }

    public List<Order> getUserOrders(Long userId) {
        return repo.findByUserId(userId);
    }
}
