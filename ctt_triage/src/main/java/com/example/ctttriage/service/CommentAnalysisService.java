package com.example.ctttriage.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.ctttriage.dto.external.ExternalTicketData;
import com.example.ctttriage.model.Comment;
import com.example.ctttriage.model.TicketCategory;
import com.example.ctttriage.model.TicketPriority;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;

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

    private final RestTemplate restTemplate = new RestTemplate();

    public boolean shouldCommentBecomeTicket (Comment comment) {

        Map<String, Object> requestBody = Map.of(
            "inputs", comment,
            "parameters", Map.of("candidate_labels", "support ticket,other")
        );
        
            ResponseEntity<Map> response = restTemplate.postForEntity(deciderUrl, buildEntity(requestBody), Map.class);
            List<Double> scores = (List<Double>) response.getBody().get("scores");
            if (scores.get(0) >= ticketThreshold){//the first score will be for "support ticket"
                return true;
            }
            else{
                return false;
            }
        
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

    //helpers

    private HttpEntity<Map> buildEntity(Map requestBody) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);
        return new HttpEntity<>(requestBody, headers);
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
