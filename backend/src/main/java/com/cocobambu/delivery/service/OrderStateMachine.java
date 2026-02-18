package com.cocobambu.delivery.service;

import com.cocobambu.delivery.model.StatusName;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class OrderStateMachine {

    private static final Map<StatusName, Set<StatusName>> TRANSITIONS = new EnumMap<>(StatusName.class);

    static {
        TRANSITIONS.put(StatusName.RECEIVED, EnumSet.of(StatusName.CONFIRMED, StatusName.CANCELED));
        TRANSITIONS.put(StatusName.CONFIRMED, EnumSet.of(StatusName.DISPATCHED, StatusName.CANCELED));
        TRANSITIONS.put(StatusName.DISPATCHED, EnumSet.of(StatusName.DELIVERED, StatusName.CANCELED));
        TRANSITIONS.put(StatusName.DELIVERED, EnumSet.noneOf(StatusName.class));
        TRANSITIONS.put(StatusName.CANCELED, EnumSet.noneOf(StatusName.class));
    }

    public boolean canTransition(StatusName from, StatusName to) {
        Set<StatusName> allowed = TRANSITIONS.get(from);
        return allowed != null && allowed.contains(to);
    }

    public Set<StatusName> getValidTransitions(StatusName from) {
        return Collections.unmodifiableSet(
                TRANSITIONS.getOrDefault(from, EnumSet.noneOf(StatusName.class))
        );
    }

    public void validateTransition(StatusName from, StatusName to) {
        if (!canTransition(from, to)) {
            throw new IllegalStateException(
                    String.format("Transição de status inválida de %s para %s. Transições válidas: %s",
                            from, to, getValidTransitions(from))
            );
        }
    }
}
