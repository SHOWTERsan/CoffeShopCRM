package ru.santurov.coffeeCRM.models;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("ORDER_REGISTERED")
public class OrderRegisteredEvent extends OrderEvent{
    private int clientId;
    private int productId;
    private LocalDateTime estimatedReadyTime;
    private BigDecimal cost;
}
