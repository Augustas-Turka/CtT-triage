package com.example.ctttriage.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ctttriage.model.Ticket;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, UUID> {

//ootb- https://docs.spring.io/spring-data/jpa/docs/current/api/org/springframework/data/jpa/repository/JpaRepository.html

}


