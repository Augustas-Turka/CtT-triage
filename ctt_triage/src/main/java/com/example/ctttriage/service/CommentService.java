package com.example.ctttriage.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import com.example.ctttriage.repositories.CommentRepository;
import com.example.ctttriage.repositories.TicketRepository;
import com.example.ctttriage.service.CommentAnalysisService;
import com.google.gson.JsonParser;
import com.example.ctttriage.model.Comment;
import com.example.ctttriage.model.Ticket;
import com.example.ctttriage.dto.CommentResponse;
import com.example.ctttriage.dto.CommentReviewRequest;
import com.example.ctttriage.dto.TicketDetailResponse;
import com.example.ctttriage.dto.external.ExternalTicketData;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final TicketRepository ticketRepository;
    private final CommentAnalysisService analysisService;

    public List<CommentResponse> getAllComments () {

        return commentRepository.findAll().stream().map(this::mapCommentToCommentResponse).toList();
    }

    public CommentResponse getComment (Long id) {

        Comment comment = commentRepository.findById(id).orElseThrow(() -> new RuntimeException("Comment not found"));
        return mapCommentToCommentResponse(comment);
    }

    public List<TicketDetailResponse> createTickets(CommentReviewRequest request) {

        Comment comment = mapCommentRequestToComment(request);

        // using a zero-shot classification model to decide if comment should become a ticket. In theory, should save on api costs over time. See README.
        if(analysisService.shouldCommentBecomeTicket(comment)) {
            log.debug("Comment {} should become a ticket", comment.getBody());

            Comment savedComment=commentRepository.save(comment);//saving comment to db to assign and id. if not ticket, comment wont be saved.

            List<ExternalTicketData> tickets = analysisService.buildTicketList(savedComment);

            List<TicketDetailResponse> ticketResponses = new ArrayList<>();
                tickets.forEach(ticket -> {
                Ticket entity = mapExternalTicketDataToTicket(ticket, savedComment);
                Ticket saved = ticketRepository.save(entity);
                ticketResponses.add(mapTicketToTicketDetailResponse(saved));
            });

            return ticketResponses;
        }

        return null;
    }

//mapping methods

    private CommentResponse mapCommentToCommentResponse(Comment comment) {

        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        response.setBody(comment.getBody());
        return response;
    }

    private TicketDetailResponse mapTicketToTicketDetailResponse(Ticket ticket){

        TicketDetailResponse response = new TicketDetailResponse();
        response.setId(ticket.getId());
        response.setTitle(ticket.getTitle());
        response.setCategory(ticket.getCategory());
        response.setPriority(ticket.getPriority());
        response.setSummary(ticket.getSummary());
        response.setSourceComment(mapCommentToCommentResponse(ticket.getSourceComment()));
        return response;
    }
    

    private Ticket mapExternalTicketDataToTicket(ExternalTicketData data, Comment comment) {

        Ticket ticket = new Ticket();
        ticket.setTitle(data.getTitle());
        ticket.setSummary(data.getSummary());
        ticket.setCategory(data.getCategory());
        ticket.setPriority(data.getPriority());
        ticket.setSourceComment(comment);

        return ticket;
    }

    private Comment mapCommentRequestToComment (CommentReviewRequest request){
        Comment comment = new Comment();
        comment.setBody(request.getBody());
        return comment;
    }
    
}
