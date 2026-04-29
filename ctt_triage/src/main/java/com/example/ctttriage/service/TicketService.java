package com.example.ctttriage.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import com.example.ctttriage.model.*;//all for now
import com.example.ctttriage.repositories.TicketRepository;
import com.example.ctttriage.dto.*;//all objects for now


@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;

    public TicketDetailResponse getTicket (UUID id) {

        Ticket ticket = ticketRepository.findById(id).orElseThrow(() -> new RuntimeException("Ticket not found"));
        return mapTicketToTicketDetailResponse(ticket);
    }

    public List<TicketSummaryResponse> getAllTickets () {

        return ticketRepository.findAll().stream().map(this::mapTicketToTicketSummaryResponse).toList();
    }

    private TicketDetailResponse mapTicketToTicketDetailResponse(Ticket ticket){

        TicketDetailResponse response = new TicketDetailResponse();
        //response.setId(ticket.getId());   not needed since using the id of this ticket to fetch this ticket?
        response.setTitle(ticket.getTitle());
        response.setCategory(ticket.getCategory());
        response.setPriority(ticket.getPriority());
        response.setSummary(ticket.getSummary());
        response.setSourceComment(mapCommentToCommentResponse(ticket.getSourceComment()));
        return response;
    }

    private CommentResponse mapCommentToCommentResponse(Comment comment) {

        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        response.setBody(comment.getBody());
        return response;
    }

    private TicketSummaryResponse mapTicketToTicketSummaryResponse(Ticket ticket) {

        TicketSummaryResponse response = new TicketSummaryResponse();
        response.setTitle(ticket.getTitle());
        response.setCategory(ticket.getCategory());
        response.setPriority(ticket.getPriority());
        response.setSummary(ticket.getSummary());
        return response;
    } 
    
    
}