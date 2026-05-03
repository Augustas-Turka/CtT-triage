package com.example.ctttriage.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.ctttriage.dto.TicketDetailResponse;
import com.example.ctttriage.dto.TicketSummaryResponse;
import com.example.ctttriage.service.TicketService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @GetMapping
    public ResponseEntity<List<TicketSummaryResponse>> getAll() {
        return ResponseEntity.ok(ticketService.getAllTickets());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketDetailResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ticketService.getTicket(id));
    }
    
}
