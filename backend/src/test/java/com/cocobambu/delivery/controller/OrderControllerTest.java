package com.cocobambu.delivery.controller;

import com.cocobambu.delivery.exception.InvalidStatusTransitionException;
import com.cocobambu.delivery.exception.OrderNotFoundException;
import com.cocobambu.delivery.model.*;
import com.cocobambu.delivery.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrderService orderService;

    private OrderWrapper createSampleOrder() {
        Order order = new Order();
        order.setOrderId("abc-123");
        order.setLastStatusName("RECEIVED");
        order.setCreatedAt(1770842000000L);
        order.setTotalPrice(89.90);
        order.setCustomer(new Customer("+5561999999", "Test User"));
        order.setStore(new Store("Loja Teste", "store-123"));
        order.setItems(List.of(new Item(1, 89.90, null, 89.90, "Item", 1, 0, List.of())));
        order.setPayments(List.of(new Payment(true, 89.90, "PIX")));
        order.setStatuses(List.of(new OrderStatus(1770842000000L, "RECEIVED", "abc-123", "STORE")));
        order.setDeliveryAddress(new DeliveryAddress(
                "Ref", "Rua", "70000", "BR", "BSB", "Centro", "1", "DF",
                new Coordinates(-47.9, -15.8, 1)
        ));

        OrderWrapper wrapper = new OrderWrapper();
        wrapper.setOrderId("abc-123");
        wrapper.setStoreId("store-123");
        wrapper.setOrder(order);
        return wrapper;
    }

    @Test
    void listAll_ReturnsOkWithOrders() throws Exception {
        when(orderService.findAll()).thenReturn(List.of(createSampleOrder()));

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].order_id").value("abc-123"));
    }

    @Test
    void getById_ExistingId_ReturnsOk() throws Exception {
        when(orderService.findById("abc-123")).thenReturn(createSampleOrder());

        mockMvc.perform(get("/api/orders/abc-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.order_id").value("abc-123"))
                .andExpect(jsonPath("$.order.customer.name").value("Test User"));
    }

    @Test
    void getById_NonExistingId_Returns404() throws Exception {
        when(orderService.findById("non-existing"))
                .thenThrow(new OrderNotFoundException("non-existing"));

        mockMvc.perform(get("/api/orders/non-existing"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void create_ValidBody_Returns201() throws Exception {
        OrderWrapper sample = createSampleOrder();
        when(orderService.create(any(OrderWrapper.class))).thenReturn(sample);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sample)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.order_id").value("abc-123"));
    }

    @Test
    void updateStatus_ValidTransition_ReturnsOk() throws Exception {
        OrderWrapper updated = createSampleOrder();
        updated.getOrder().setLastStatusName("CONFIRMED");
        when(orderService.updateStatus(eq("abc-123"), any())).thenReturn(updated);

        mockMvc.perform(patch("/api/orders/abc-123/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"CONFIRMED\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.order.last_status_name").value("CONFIRMED"));
    }

    @Test
    void updateStatus_InvalidTransition_Returns400() throws Exception {
        when(orderService.updateStatus(eq("abc-123"), any()))
                .thenThrow(new InvalidStatusTransitionException("Transicao invalida"));

        mockMvc.perform(patch("/api/orders/abc-123/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"DELIVERED\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Transicao invalida"));
    }

    @Test
    void delete_ExistingId_Returns204() throws Exception {
        mockMvc.perform(delete("/api/orders/abc-123"))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_NonExistingId_Returns404() throws Exception {
        doThrow(new OrderNotFoundException("non-existing"))
                .when(orderService).delete("non-existing");

        mockMvc.perform(delete("/api/orders/non-existing"))
                .andExpect(status().isNotFound());
    }
}
