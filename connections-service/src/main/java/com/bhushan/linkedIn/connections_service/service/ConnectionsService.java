package com.bhushan.linkedIn.connections_service.service;

import com.bhushan.linkedIn.connections_service.auth.UserContextHolder;
import com.bhushan.linkedIn.connections_service.entity.Person;
import com.bhushan.linkedIn.connections_service.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;


@RequiredArgsConstructor
@Slf4j
@Service
public class ConnectionsService {

    private final PersonRepository personRepository;

    public List<Person> getFirstDegreeConnections(){

        Long userId  = UserContextHolder.getCurrentUserId();

        log.info("getting first degree connections for userId : "+userId);
        return personRepository.getFirstDegreeConnections(userId);
    }

}
