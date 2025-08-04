package com.backend.immilog.shared.domain.event;

import com.backend.immilog.shared.infrastructure.event.RedisEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class DomainEvents implements ApplicationContextAware {

    private static ApplicationContext applicationContext;
    private static final ThreadLocal<List<DomainEvent>> events = ThreadLocal.withInitial(ArrayList::new);

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        DomainEvents.applicationContext = applicationContext;
    }

    public static void raise(DomainEvent event) {
        try {
            if (applicationContext != null) {
                RedisEventPublisher publisher = applicationContext.getBean(RedisEventPublisher.class);
                publisher.publishDomainEvent(event);
                log.debug("Immediately published domain event: {}", event.getClass().getSimpleName());
            } else {
                events.get().add(event);
                log.warn("ApplicationContext not available, storing event in ThreadLocal: {}",
                        event.getClass().getSimpleName());
            }
        } catch (Exception e) {
            log.error("Failed to publish domain event: {}, falling back to ThreadLocal",
                    event.getClass().getSimpleName(), e);
            events.get().add(event);
        }
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

    public static void raiseCompensationEvent(DomainEvent event) {
        try {
            if (applicationContext != null) {
                var publisher = applicationContext.getBean(RedisEventPublisher.class);
                publisher.publishCompensationEvent(event);
                log.debug("Immediately published compensation event: {}", event.getClass().getSimpleName());
            } else {
                log.error("Cannot publish compensation event - ApplicationContext not available: {}", event.getClass().getSimpleName());
            }
        } catch (Exception e) {
            log.error("Failed to publish compensation event: {}", event.getClass().getSimpleName(), e);
            throw new RuntimeException("Failed to publish compensation event", e);
        }
    }
}