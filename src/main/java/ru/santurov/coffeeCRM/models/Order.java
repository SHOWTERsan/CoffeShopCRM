package ru.santurov.coffeeCRM.models;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class Order {
    private int id;
    private String status;
    private int clientId;
    private int productId;
    private int orderId;
    private int employeeId;
    private BigDecimal productCost;
    private LocalDateTime estimatedDeliveryTime;
    private LocalDateTime completionTime;
    private String cancellationReason;
    private List<OrderEvent> events = new ArrayList<>();;

    public void addEvent(OrderEvent event) {
        events.add(event);
    }
}
