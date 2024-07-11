package ru.santurov.coffeeCRM;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.santurov.coffeeCRM.controllers.OrderEventController;
import ru.santurov.coffeeCRM.models.Order;
import ru.santurov.coffeeCRM.models.OrderEvent;
import ru.santurov.coffeeCRM.models.OrderRegisteredEvent;
import ru.santurov.coffeeCRM.services.OrderServiceImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderEventControllerTest {

    @Mock
    private OrderServiceImpl orderService;

    @InjectMocks
    private OrderEventController orderEventController;

    @Test
    void testPublishEvent() {
        OrderEvent event = new OrderRegisteredEvent();
        doNothing().when(orderService).publishEvent(event);

        ResponseEntity<?> response = orderEventController.publishEvent(event);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testPublishEventWithException() {
        OrderEvent event = new OrderRegisteredEvent();
        doThrow(new IllegalArgumentException("Error")).when(orderService).publishEvent(event);

        ResponseEntity<?> response = orderEventController.publishEvent(event);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Error", response.getBody());
    }

    @Test
    void testFindOrder() {
        Order order = new Order();
        order.setId(1);
        when(orderService.findOrder(1)).thenReturn(order);

        ResponseEntity<Order> response = orderEventController.findOrder(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(order, response.getBody());
    }

    @Test
    void testFindOrderNotFound() {
        when(orderService.findOrder(1)).thenReturn(null);

        ResponseEntity<Order> response = orderEventController.findOrder(1);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
