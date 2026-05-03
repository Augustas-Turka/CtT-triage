package com.example.ctttriage.service;

import org.springframework.stereotype.Service;

import com.example.ctttriage.dto.external.ExternalTicketData;
import com.example.ctttriage.model.Comment;
import com.example.ctttriage.model.TicketCategory;
import com.example.ctttriage.model.TicketPriority;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;

@Service
@RequiredArgsConstructor
public class CommentAnalysisService {

    @Value("${huggingface.api.key}")
    private String apiKey;

    @Value("${huggingface.decider.url}")
    private String deciderUrl;

    @Value("${huggingface.generator.url}")
    private String generatorUrl;

    @Value("${ticket.threshold}")
    private double ticketThreshold;


    public boolean shouldCommentBecomeTicket (Comment comment) {
        //api call to decide if comment should become a ticket.
        return true;
    }

    public List<ExternalTicketData> buildTicketList(Comment comment) {

    String apiResponse = "api response";//TODO: placeholder
    JsonArray jsonArray = JsonParser.parseString(apiResponse).getAsJsonArray();

    List<ExternalTicketData> tickets = new ArrayList<>();
    for (JsonElement element : jsonArray) {
        JsonObject json = element.getAsJsonObject();
        tickets.add(mapJsonResponseToExternalTicketData(json));
    }
    return tickets;
    }

    //mapping

    private ExternalTicketData mapJsonResponseToExternalTicketData (JsonObject json) {

        ExternalTicketData response = new ExternalTicketData();
        response.setTitle(json.get("title").getAsString());
        response.setSummary(json.get("summary").getAsString());
        response.setCategory(TicketCategory.valueOf(json.get("category").getAsString()));
        response.setPriority(TicketPriority.valueOf(json.get("priority").getAsString()));

        return response;
    }




    
}
