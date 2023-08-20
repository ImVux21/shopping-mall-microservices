package com.programming.imvux21.notificationservice;

import com.programming.imvux21.notificationservice.event.OrderPlacedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.KafkaListener;

@SpringBootApplication
@Slf4j
public class NotificationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }

    @KafkaListener(topics = "notification-topic", groupId = "notification-group-id")
    public void handleNotification(OrderPlacedEvent orderPlacedEvent) {
        // TODO: send email notification
        log.info("Notification sent for order id: {}", orderPlacedEvent.getOrderNumber());
    }
}
