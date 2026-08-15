package com.bhushan.linkedIn.connections_service.service;

import com.bhushan.linkedIn.connections_service.entity.Person;
import com.bhushan.linkedIn.connections_service.event.UserCreatedEvent;
import com.bhushan.linkedIn.connections_service.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserCreatedConsumer {

    private final PersonRepository personRepository;

    @KafkaListener(topics = "user-created-topic")
    public void handleUserCreated(UserCreatedEvent event) {

        log.info("Received UserCreatedEvent: {}", event);

        Person person = new Person();
        person.setUserId(event.getUserId());
        person.setName(event.getName());

        personRepository.save(person);

        log.info("Created Person node for userId: {}", event.getUserId());
    }
}