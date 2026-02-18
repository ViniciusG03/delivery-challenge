package com.cocobambu.delivery.service;

import com.cocobambu.delivery.dto.StatusUpdateRequest;
import com.cocobambu.delivery.exception.InvalidStatusTransitionException;
import com.cocobambu.delivery.exception.OrderNotFoundException;
import com.cocobambu.delivery.model.*;
import com.cocobambu.delivery.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderStateMachine stateMachine;

    public OrderService(OrderRepository orderRepository, OrderStateMachine stateMachine) {
        this.orderRepository = orderRepository;
        this.stateMachine = stateMachine;
    }

    public List<OrderWrapper> findAll() {
        return orderRepository.findAll();
    }

    public OrderWrapper findById(String id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
    }

    public OrderWrapper create(OrderWrapper orderWrapper) {
        String orderId = UUID.randomUUID().toString();
        long now = System.currentTimeMillis();

        orderWrapper.setOrderId(orderId);

        Order order = orderWrapper.getOrder();
        order.setOrderId(orderId);
        order.setCreatedAt(now);
        order.setLastStatusName(StatusName.RECEIVED.name());

        if (order.getStore() != null) {
            orderWrapper.setStoreId(order.getStore().getId());
        }

        OrderStatus initialStatus = new OrderStatus(now, StatusName.RECEIVED.name(), orderId, "API");
        order.setStatuses(new ArrayList<>(List.of(initialStatus)));

        return orderRepository.save(orderWrapper);
    }

    public OrderWrapper update(String id, OrderWrapper orderWrapper) {
        OrderWrapper existing = findById(id);

        orderWrapper.setOrderId(existing.getOrderId());
        orderWrapper.getOrder().setOrderId(existing.getOrderId());

        return orderRepository.save(orderWrapper);
    }

    public void delete(String id) {
        if (!orderRepository.deleteById(id)) {
            throw new OrderNotFoundException(id);
        }
    }

    public OrderWrapper updateStatus(String id, StatusUpdateRequest request) {
        OrderWrapper orderWrapper = findById(id);
        Order order = orderWrapper.getOrder();

        StatusName currentStatus;
        try {
            currentStatus = StatusName.valueOf(order.getLastStatusName());
        } catch (IllegalArgumentException e) {
            throw new InvalidStatusTransitionException(
                    "Status atual desconhecido: " + order.getLastStatusName());
        }

        StatusName targetStatus;
        try {
            targetStatus = StatusName.valueOf(request.getStatus());
        } catch (IllegalArgumentException e) {
            throw new InvalidStatusTransitionException(
                    "Status alvo inválido: " + request.getStatus());
        }

        try {
            stateMachine.validateTransition(currentStatus, targetStatus);
        } catch (IllegalStateException e) {
            throw new InvalidStatusTransitionException(e.getMessage());
        }

        OrderStatus newStatus = new OrderStatus(
                System.currentTimeMillis(),
                targetStatus.name(),
                id,
                "API"
        );

        if (order.getStatuses() == null) {
            order.setStatuses(new ArrayList<>());
        }
        order.getStatuses().add(newStatus);
        order.setLastStatusName(targetStatus.name());

        return orderRepository.save(orderWrapper);
    }
}
