package ru.santurov.coffeeCRM.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.santurov.coffeeCRM.models.*;
import ru.santurov.coffeeCRM.repositories.OrderEventRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderEventRepository eventRepository;

    @Override
    public void publishEvent(OrderEvent event) {
        List<OrderEvent> events = eventRepository.findByOrderId(event.getOrderId());

        boolean isRegistered = events.stream().anyMatch(i -> i instanceof OrderRegisteredEvent);
        boolean isDone = events.stream().anyMatch(i -> i instanceof OrderCancelledEvent || i instanceof OrderIssuedEvent);

        if (!isRegistered && !(event instanceof OrderRegisteredEvent))
            throw new IllegalStateException("Заказ должен быть зарегистрирован до назначения других событий.");
        if (isDone)
            throw new IllegalStateException("Заказ уже завершен. Невозможно назначить событие.");

        event.setEventDate(LocalDateTime.now());
        eventRepository.save(event);

    }

    @Override
    public Order findOrder(int id) {
        List<OrderEvent> events = eventRepository.findByOrderId(id);
        if (events.isEmpty())
            return null;

        Order order = new Order();
        order.setId(id);

        for (OrderEvent event : events) {
            if (event instanceof OrderRegisteredEvent) {
                order.setStatus(event.getEventType());
                order.setEstimatedDeliveryTime(((OrderRegisteredEvent) event).getEstimatedDeliveryTime());
                order.setProductId(((OrderRegisteredEvent) event).getProductId());
                order.setProductCost(((OrderRegisteredEvent) event).getProductCost());
            } else if (event instanceof OrderCancelledEvent) {
                order.setStatus(event.getEventType());
                order.setCancellationReason(((OrderCancelledEvent) event).getReason());
            } else if (event instanceof OrderTakenEvent) {
                order.setStatus(event.getEventType());
            } else if (event instanceof OrderReadyEvent) {
                order.setStatus(event.getEventType());
            } else if (event instanceof OrderIssuedEvent) {
                order.setStatus(event.getEventType());
            }
            toOrder(order, event);
        }
        return order;
    }

    private void toOrder(Order futureOrder, OrderEvent event) {
        futureOrder.setOrderId(event.getOrderId());
        futureOrder.setEmployeeId(event.getEmployeeId());
        futureOrder.addEvent(event);
        if (!(event instanceof OrderRegisteredEvent)) {
            futureOrder.setCompletionTime(event.getEventDate());
        }
    }
}
