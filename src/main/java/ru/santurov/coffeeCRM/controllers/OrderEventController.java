package ru.santurov.coffeeCRM.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.santurov.coffeeCRM.models.Order;
import ru.santurov.coffeeCRM.models.OrderEvent;
import ru.santurov.coffeeCRM.services.OrderServiceImpl;

@RestController
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderEventController {
    private final OrderServiceImpl orderService;

    @PostMapping("/event")
    public ResponseEntity<Void> publishEvent(@RequestBody OrderEvent event) {
        try {
            orderService.publishEvent(event);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> findOrder(@PathVariable int id) {
        Order order = orderService.findOrder(id);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(order);
    }
}
