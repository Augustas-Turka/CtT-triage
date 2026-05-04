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
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;

@Slf4j
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

    private static final String PROMPT = """
        You review comments that have to become support tickets.
        For every ticket that can be created from the comment (one or more),
            you will have to decide on it's category, priority and generate a title and a short summary describing the issue.
        The categories to pick from- BUG | FEATURE | BILLING | ACCOUNT | OTHER
        The priorities to pick from- LOW | MEDIUM | HIGH | CRITICAL
        Respond ONLY with a JSON array- no additional text or symbols
        [{ "title": "...", "category": "BUG | FEATURE | BILLING | ACCOUNT | OTHER", "priority": "LOW | MEDIUM | HIGH | CRITICAL", "summary": "..." }]
        Comment: "%s"
        """;

    private final RestTemplate restTemplate = new RestTemplate();

    public boolean shouldCommentBecomeTicket (Comment comment) {
        log.debug("Checking if comment [id={}] should become a ticket. Body: '{}'", comment.getId(), comment.getBody());

        Map<String, Object> requestBody = Map.of(
            "inputs", comment.getBody(),
            "parameters", Map.of("candidate_labels", "support ticket,other")
        );

        log.debug("Request body: {}", requestBody);
        
        ResponseEntity<List> response = restTemplate.postForEntity(deciderUrl, buildEntity(requestBody), List.class);

        List<Map<String, Object>> results = response.getBody();
        String topLabel = (String) results.get(0).get("label");
        double topScore = (Double) results.get(0).get("score");

        log.debug("Top label: {}, score: {}", topLabel, topScore);

        if ((topLabel == "support ticket") && (topScore > 0.75)){
            return true;
        }

        return false;
        
    }

    public List<ExternalTicketData> buildTicketList(Comment comment) {

    String apiResponse = "api response";//TODO: placeholder
    log.debug("Generator raw response: {}", apiResponse);
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
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
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
