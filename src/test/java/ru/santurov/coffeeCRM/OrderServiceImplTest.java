package ru.santurov.coffeeCRM;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.santurov.coffeeCRM.models.*;
import ru.santurov.coffeeCRM.repositories.OrderEventRepository;
import ru.santurov.coffeeCRM.services.OrderServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {
    @Mock
    private OrderEventRepository eventRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void publishEvent() {
        OrderEvent event = new OrderRegisteredEvent();
        event.setOrderId(1);
        when(eventRepository.findByOrderId(1)).thenReturn(Collections.emptyList());

        orderService.publishEvent(event);

        assertNotNull(event.getEventDate());
        verify(eventRepository, times(1)).save(event);
    }

    @Test
    void publishEventWithValidationForIsDone() {
        OrderEvent existingEvent = new OrderRegisteredEvent();
        OrderEvent cancelledEvent = new OrderCancelledEvent();
        OrderEvent newEvent = new OrderReadyEvent();
        existingEvent.setOrderId(1);
        cancelledEvent.setOrderId(1);
        newEvent.setOrderId(1);

        when(eventRepository.findByOrderId(1)).thenReturn(List.of(existingEvent,cancelledEvent));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> orderService.publishEvent(newEvent));

        assertEquals("Заказ уже завершен. Невозможно назначить событие.", exception.getMessage());
    }

    @Test
    void publishEventWithValidationForAlreadyExist() {
        OrderEvent existingEvent = new OrderRegisteredEvent();
        OrderEvent newEvent = new OrderRegisteredEvent();
        existingEvent.setOrderId(1);
        newEvent.setOrderId(1);

        when(eventRepository.findByOrderId(1)).thenReturn(List.of(existingEvent));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> orderService.publishEvent(newEvent));

        assertEquals("Событие уже существует.",exception.getMessage());
    }

    @Test
    void publishEventWithoutRegistration() {
        OrderEvent newEvent = new OrderReadyEvent();
        newEvent.setOrderId(1);

        when(eventRepository.findByOrderId(1)).thenReturn(Collections.emptyList());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> orderService.publishEvent(newEvent));
        assertEquals("Заказ должен быть зарегистрирован до назначения других событий.",exception.getMessage());
    }

    @Test
    void publishReadyEventWithoutTakenEvent() {
        OrderEvent registeredEvent = new OrderRegisteredEvent();
        OrderEvent readyEvent = new OrderReadyEvent();
        readyEvent.setOrderId(1);
        registeredEvent.setOrderId(1);

        when(eventRepository.findByOrderId(1)).thenReturn(List.of(registeredEvent));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> orderService.publishEvent(readyEvent));
        assertEquals("Невозможно объявить готовность, если заказ не был взять в работу.",exception.getMessage());
    }

    @Test
    void publishIssuedEventWithoutReadyEvent() {
        OrderEvent registeredEvent = new OrderRegisteredEvent();
        OrderEvent issuedEvent = new OrderIssuedEvent();
        issuedEvent.setOrderId(1);
        registeredEvent.setOrderId(1);

        when(eventRepository.findByOrderId(1)).thenReturn(List.of(registeredEvent));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> orderService.publishEvent(issuedEvent));
        assertEquals("Невозможно выдать заказ, который не готов к выдаче.",exception.getMessage());
    }

    @Test
    void findOrder() {
        OrderRegisteredEvent registeredEvent = new OrderRegisteredEvent();
        registeredEvent.setOrderId(1);
        registeredEvent.setEventType("ORDER_REGISTERED");
        registeredEvent.setProductId(123);
        registeredEvent.setProductCost(BigDecimal.valueOf(500));
        registeredEvent.setEstimatedDeliveryTime(LocalDateTime.now().plusDays(1));

        when(eventRepository.findByOrderId(1)).thenReturn(List.of(registeredEvent));

        Order order = orderService.findOrder(1);

        assertNotNull(order);
        assertEquals(1, order.getId());
        assertEquals(123, order.getProductId());
        assertEquals(BigDecimal.valueOf(500), order.getProductCost());
        System.out.println(order.getStatus());
    }
}