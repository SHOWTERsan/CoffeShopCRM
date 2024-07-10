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
        validateEvent(event);

        event.setEventDate(LocalDateTime.now());
        eventRepository.save(event);
    }

    private void validateEvent(OrderEvent event) {
        List<OrderEvent> events = eventRepository.findByOrderId(event.getOrderId());

        boolean isRegistered = events.stream().anyMatch(i -> i instanceof OrderRegisteredEvent);
        boolean isDone = events.stream().anyMatch(i -> i instanceof OrderCancelledEvent || i instanceof OrderIssuedEvent);
        boolean isExists = events.stream().anyMatch(i -> event.getClass().isInstance(i));
        boolean isTaken = events.stream().anyMatch(i -> i instanceof OrderTakenEvent);
        boolean isReady = events.stream().anyMatch(i -> i instanceof OrderReadyEvent);

        if (isDone)
            throw new IllegalArgumentException("Заказ уже завершен. Невозможно назначить событие.");
        if (isExists)
            throw new IllegalArgumentException("Событие уже существует.");
        if (!isRegistered && !(event instanceof OrderRegisteredEvent))
            throw new IllegalArgumentException("Заказ должен быть зарегистрирован до назначения других событий.");
        if (event instanceof OrderReadyEvent && !isTaken)
            throw new IllegalArgumentException("Невозможно объявить готовность, если заказ не был взять в работу.");
        if (event instanceof OrderIssuedEvent && !isReady)
            throw new IllegalArgumentException("Невозможно выдать заказ, который не готов к выдаче.");
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
