package com.ecommerce.service;

import com.ecommerce.entity.Order;
import com.ecommerce.entity.User;
import com.ecommerce.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public List<Order> getOrdersByUser(User user) {
        return orderRepository.findByUserId(user.getId());
    }
    
    public Order createOrder(Order order) {
        return orderRepository.save(order);
    }
    
    public void updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order != null) {
            order.setStatus(status);
            orderRepository.save(order);
        }
    }
}