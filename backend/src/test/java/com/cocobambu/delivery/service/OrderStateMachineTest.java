package com.cocobambu.delivery.service;

import com.cocobambu.delivery.model.StatusName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class OrderStateMachineTest {

    private final OrderStateMachine stateMachine = new OrderStateMachine();

    @ParameterizedTest
    @CsvSource({
            "RECEIVED, CONFIRMED",
            "RECEIVED, CANCELED",
            "CONFIRMED, DISPATCHED",
            "CONFIRMED, CANCELED",
            "DISPATCHED, DELIVERED",
            "DISPATCHED, CANCELED"
    })
    void canTransition_ValidTransition_ReturnsTrue(String from, String to) {
        assertTrue(stateMachine.canTransition(StatusName.valueOf(from), StatusName.valueOf(to)));
    }

    @ParameterizedTest
    @CsvSource({
            "RECEIVED, DISPATCHED",
            "RECEIVED, DELIVERED",
            "CONFIRMED, RECEIVED",
            "DISPATCHED, CONFIRMED",
            "DISPATCHED, RECEIVED",
            "DELIVERED, RECEIVED",
            "DELIVERED, CONFIRMED",
            "DELIVERED, DISPATCHED",
            "DELIVERED, CANCELED",
            "CANCELED, RECEIVED",
            "CANCELED, CONFIRMED",
            "CANCELED, DISPATCHED",
            "CANCELED, DELIVERED"
    })
    void canTransition_InvalidTransition_ReturnsFalse(String from, String to) {
        assertFalse(stateMachine.canTransition(StatusName.valueOf(from), StatusName.valueOf(to)));
    }

    @Test
    void getValidTransitions_DeliveredStatus_ReturnsEmptySet() {
        assertTrue(stateMachine.getValidTransitions(StatusName.DELIVERED).isEmpty());
    }

    @Test
    void getValidTransitions_CanceledStatus_ReturnsEmptySet() {
        assertTrue(stateMachine.getValidTransitions(StatusName.CANCELED).isEmpty());
    }

    @Test
    void getValidTransitions_ReceivedStatus_ReturnsConfirmedAndCanceled() {
        var transitions = stateMachine.getValidTransitions(StatusName.RECEIVED);
        assertEquals(2, transitions.size());
        assertTrue(transitions.contains(StatusName.CONFIRMED));
        assertTrue(transitions.contains(StatusName.CANCELED));
    }

    @Test
    void validateTransition_ValidTransition_DoesNotThrow() {
        assertDoesNotThrow(() ->
                stateMachine.validateTransition(StatusName.RECEIVED, StatusName.CONFIRMED)
        );
    }

    @Test
    void validateTransition_InvalidTransition_ThrowsIllegalStateException() {
        assertThrows(IllegalStateException.class, () ->
                stateMachine.validateTransition(StatusName.DELIVERED, StatusName.RECEIVED)
        );
    }
}
