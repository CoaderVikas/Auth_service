package com.vikas.kafka.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.vikas.event.UserRegisteredEvent;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

/**
 * Class      : UserEventProducer
 * Description: [Add brief description here]
 * Author     : Vikas Yadav
 * Created On : Mar 7, 2026
 * Version    : 1.0
 */

@Service
@RequiredArgsConstructor
public class UserEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendUserRegisteredEvent(UserRegisteredEvent event) {

        kafkaTemplate.send("user-registration", event);

        System.out.println("Event sent to Kafka: " + event.getEmail());
    }
}
