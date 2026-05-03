package com.example.ctttriage.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import com.example.ctttriage.repositories.CommentRepository;
import com.example.ctttriage.repositories.TicketRepository;
import com.example.ctttriage.service.CommentAnalysisService;
import com.google.gson.JsonParser;
import com.example.ctttriage.model.*;//all for now
import com.example.ctttriage.dto.*;//all objects for now
import com.example.ctttriage.dto.external.ExternalTicketData;


import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final TicketRepository ticketRepository;
    private final CommentAnalysisService analysisService;

    public List<CommentResponse> getAllComments () {

        return commentRepository.findAll().stream().map(this::mapCommentToCommentResponse).toList();
    }

    public CommentResponse getComment (UUID id) {

        Comment comment = commentRepository.findById(id).orElseThrow(() -> new RuntimeException("Comment not found"));
        return mapCommentToCommentResponse(comment);
    }

    //TODO: update return type
    public void createTickets(CommentReviewRequest request) {
        Comment comment = mapCommentRequestToComment(request);
        if(analysisService.shouldCommentBecomeTicket(comment)) {//prompt something like "does this comment contain something that should become a technical ticket", no matter how many tickets
            List<ExternalTicketData> tickets = analysisService.buildTicketList(comment);
            tickets.forEach(ticket -> {
                Ticket entity = mapExternalTicketDataToTicket(ticket);
                ticketRepository.save(entity);
        });
        };
    }

//mapping methods

    private CommentResponse mapCommentToCommentResponse(Comment comment) {

        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        response.setBody(comment.getBody());
        return response;
    }

    

    private Ticket mapExternalTicketDataToTicket(ExternalTicketData data) {

        Ticket ticket = new Ticket();
        ticket.setTitle(data.getTitle());
        ticket.setSummary(data.getSummary());
        ticket.setCategory(data.getCategory());
        ticket.setPriority(data.getPriority());

        return ticket;
    }

    private Comment mapCommentRequestToComment (CommentReviewRequest request){
        Comment comment = new Comment();
        comment.setBody(request.getBody());
        return comment;
    }
    
}
