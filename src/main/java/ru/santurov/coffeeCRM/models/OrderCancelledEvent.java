package ru.santurov.coffeeCRM.models;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("ORDER_CANCELLED")
public class OrderCancelledEvent extends OrderEvent{
    private String reason;
}
