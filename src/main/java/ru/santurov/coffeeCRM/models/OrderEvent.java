package ru.santurov.coffeeCRM.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "event_type")
public class OrderEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int orderId;
    private int employeeId;
    private LocalDateTime eventDate;

    @Column(name = "event_type", insertable = false, updatable = false)
    private String eventType;
}
