package com.example.ctttriage.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import com.example.ctttriage.repositories.CommentRepository;
import com.example.ctttriage.repositories.TicketRepository;
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

    public List<CommentResponse> getAllComments () {

        return commentRepository.findAll().stream().map(this::mapCommentToCommentResponse).toList();
    }

    //TODO: update return type
    public void createTickets(Comment comment) {
        if(shouldCommentBecomeTicket(comment)) {//prompt something like "does this comment contain something that should become a technical ticket", no matter how many tickets
            List<ExternalTicketData> tickets = buildTicketList(comment);
            tickets.forEach(ticket -> {
                Ticket entity = mapExternalTicketDataToTicket(ticket);
                ticketRepository.save(entity);
        });
        };
    }

//helper methods

    private boolean shouldCommentBecomeTicket (Comment comment) {
        //api call to decide if comment should become a ticket.
        return true;
    }

    private List<ExternalTicketData> buildTicketList(Comment comment) {

    String apiResponse = "api response";//TODO: placeholder
    JsonArray jsonArray = JsonParser.parseString(apiResponse).getAsJsonArray();

    List<ExternalTicketData> tickets = new ArrayList<>();
    for (JsonElement element : jsonArray) {
        JsonObject json = element.getAsJsonObject();
        tickets.add(mapJsonResponseToExternalTicketData(json));
    }
    return tickets;
}

//mapping methods

    private CommentResponse mapCommentToCommentResponse(Comment comment) {

        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        response.setBody(comment.getBody());
        return response;
    }

    private ExternalTicketData mapJsonResponseToExternalTicketData (JsonObject json) {

        ExternalTicketData response = new ExternalTicketData();
        response.setTitle(json.get("title").getAsString());
        response.setSummary(json.get("summary").getAsString());
        response.setCategory(TicketCategory.valueOf(json.get("category").getAsString()));
        response.setPriority(TicketPriority.valueOf(json.get("priority").getAsString()));

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
    
}
