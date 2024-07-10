package ru.santurov.coffeeCRM.services;

import ru.santurov.coffeeCRM.models.Order;
import ru.santurov.coffeeCRM.models.OrderEvent;

public interface OrderService {
    void publishEvent(OrderEvent event);

    Order findOrder(int id);
}
