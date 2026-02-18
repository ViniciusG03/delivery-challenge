package com.cocobambu.delivery.service;

import com.cocobambu.delivery.dto.StatusUpdateRequest;
import com.cocobambu.delivery.exception.InvalidStatusTransitionException;
import com.cocobambu.delivery.exception.OrderNotFoundException;
import com.cocobambu.delivery.model.*;
import com.cocobambu.delivery.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    private OrderWrapper sampleOrder;

    @BeforeEach
    void setUp() {
        Order order = new Order();
        order.setOrderId("test-id-123");
        order.setLastStatusName("RECEIVED");
        order.setCreatedAt(System.currentTimeMillis());
        order.setTotalPrice(89.90);
        order.setCustomer(new Customer("+5561999999", "Test User"));
        order.setStore(new Store("Loja Teste", "store-123"));
        order.setItems(List.of(new Item(1, 89.90, null, 89.90, "Item Teste", 1, 0, List.of())));
        order.setPayments(List.of(new Payment(true, 89.90, "PIX")));
        order.setStatuses(new ArrayList<>(List.of(
                new OrderStatus(System.currentTimeMillis(), "RECEIVED", "test-id-123", "STORE")
        )));
        order.setDeliveryAddress(new DeliveryAddress(
                "Ref", "Rua Teste", "70000-000", "BR", "Brasilia",
                "Centro", "100", "DF",
                new Coordinates(-47.9, -15.8, 1)
        ));

        sampleOrder = new OrderWrapper();
        sampleOrder.setOrderId("test-id-123");
        sampleOrder.setStoreId("store-123");
        sampleOrder.setOrder(order);
    }

    @Test
    void findAll_ReturnsList() {
        when(orderRepository.findAll()).thenReturn(List.of(sampleOrder));
        List<OrderWrapper> result = orderService.findAll();
        assertEquals(1, result.size());
        verify(orderRepository).findAll();
    }

    @Test
    void findById_ExistingId_ReturnsOrder() {
        when(orderRepository.findById("test-id-123")).thenReturn(Optional.of(sampleOrder));
        OrderWrapper result = orderService.findById("test-id-123");
        assertEquals("test-id-123", result.getOrderId());
    }

    @Test
    void findById_NonExistingId_ThrowsOrderNotFoundException() {
        when(orderRepository.findById("non-existing")).thenReturn(Optional.empty());
        assertThrows(OrderNotFoundException.class, () -> orderService.findById("non-existing"));
    }

    @Test
    void create_ValidOrder_SetsReceivedStatusAndGeneratesId() {
        when(orderRepository.save(any(OrderWrapper.class))).thenAnswer(i -> i.getArgument(0));

        OrderWrapper input = new OrderWrapper();
        Order inputOrder = new Order();
        inputOrder.setStore(new Store("Loja", "store-1"));
        inputOrder.setItems(List.of());
        inputOrder.setPayments(List.of());
        inputOrder.setCustomer(new Customer("+55", "User"));
        inputOrder.setDeliveryAddress(new DeliveryAddress(
                "", "Rua", "70000", "BR", "BSB", "Centro", "1", "DF",
                new Coordinates(-47, -15, 1)
        ));
        input.setOrder(inputOrder);

        OrderWrapper result = orderService.create(input);

        assertNotNull(result.getOrderId());
        assertEquals("RECEIVED", result.getOrder().getLastStatusName());
        assertEquals(1, result.getOrder().getStatuses().size());
        assertEquals("RECEIVED", result.getOrder().getStatuses().get(0).getName());
    }

    @Test
    void updateStatus_ValidTransition_UpdatesStatusAndAddsHistory() {
        when(orderRepository.findById("test-id-123")).thenReturn(Optional.of(sampleOrder));
        when(orderRepository.save(any(OrderWrapper.class))).thenAnswer(i -> i.getArgument(0));

        StatusUpdateRequest request = new StatusUpdateRequest();
        request.setStatus("CONFIRMED");

        OrderWrapper result = orderService.updateStatus("test-id-123", request);

        assertEquals("CONFIRMED", result.getOrder().getLastStatusName());
        assertEquals(2, result.getOrder().getStatuses().size());
        assertEquals("CONFIRMED", result.getOrder().getStatuses().get(1).getName());
    }

    @Test
    void updateStatus_InvalidTransition_ThrowsInvalidStatusTransitionException() {
        when(orderRepository.findById("test-id-123")).thenReturn(Optional.of(sampleOrder));

        StatusUpdateRequest request = new StatusUpdateRequest();
        request.setStatus("DELIVERED");

        assertThrows(InvalidStatusTransitionException.class, () ->
                orderService.updateStatus("test-id-123", request)
        );
    }

    @Test
    void updateStatus_TerminalState_ThrowsInvalidStatusTransitionException() {
        sampleOrder.getOrder().setLastStatusName("CANCELED");
        when(orderRepository.findById("test-id-123")).thenReturn(Optional.of(sampleOrder));

        StatusUpdateRequest request = new StatusUpdateRequest();
        request.setStatus("RECEIVED");

        assertThrows(InvalidStatusTransitionException.class, () ->
                orderService.updateStatus("test-id-123", request)
        );
    }

    @Test
    void delete_ExistingId_CallsRepository() {
        when(orderRepository.deleteById("test-id-123")).thenReturn(true);
        assertDoesNotThrow(() -> orderService.delete("test-id-123"));
        verify(orderRepository).deleteById("test-id-123");
    }

    @Test
    void delete_NonExistingId_ThrowsOrderNotFoundException() {
        when(orderRepository.deleteById("non-existing")).thenReturn(false);
        assertThrows(OrderNotFoundException.class, () -> orderService.delete("non-existing"));
    }
}
