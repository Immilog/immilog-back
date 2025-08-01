package com.backend.immilog.shared.domain.event;

import java.util.ArrayList;
import java.util.List;

public class DomainEvents {
    private static final ThreadLocal<List<DomainEvent>> events = ThreadLocal.withInitial(ArrayList::new);

    public static void raise(DomainEvent event) {
        events.get().add(event);
    }

    public static List<DomainEvent> getEvents() {
        return new ArrayList<>(events.get());
    }

    public static void clearEvents() {
        events.get().clear();
    }

    public static boolean hasEvents() {
        return !events.get().isEmpty();
    }
}