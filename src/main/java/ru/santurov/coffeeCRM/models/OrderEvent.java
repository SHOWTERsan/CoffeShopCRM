package ru.santurov.coffeeCRM.models;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "eventType"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = OrderRegisteredEvent.class, name = "ORDER_REGISTERED"),
        @JsonSubTypes.Type(value = OrderCancelledEvent.class, name = "ORDER_CANCELLED"),
        @JsonSubTypes.Type(value = OrderIssuedEvent.class, name = "ORDER_ISSUED"),
        @JsonSubTypes.Type(value = OrderReadyEvent.class, name = "ORDER_READY"),
        @JsonSubTypes.Type(value = OrderTakenEvent.class, name = "ORDER_TAKEN")
})
@Entity
@Data
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "event_type")
public abstract class OrderEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int orderId;
    private int employeeId;
    private LocalDateTime eventDate;

    @Column(name = "event_type", insertable = false, updatable = false)
    private String eventType;
}
