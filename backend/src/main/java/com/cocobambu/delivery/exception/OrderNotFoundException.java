package com.cocobambu.delivery.exception;

public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException(String orderId) {
        super("Pedido não encontrado com id: " + orderId);
    }
}
