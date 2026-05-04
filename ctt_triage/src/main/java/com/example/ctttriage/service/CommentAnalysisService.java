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
import java.util.HashMap;
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
        You review user comments and convert them into support tickets.
        A single comment may contain multiple separate issues - create one ticket per issue.
        For each ticket, assign a category, priority, and generate a short title and summary.
        Categories: BUG | FEATURE | BILLING | ACCOUNT | OTHER
        Priorities: LOW | MEDIUM | HIGH | CRITICAL
        Respond ONLY with a valid JSON array. No explanation, no markdown, no code blocks.
        Example format: [{ "title": "...", "category": "BUG", "priority": "HIGH", "summary": "..." }]
        Comment: "%s"
        """;

    private final RestTemplate restTemplate = new RestTemplate();

    public boolean shouldCommentBecomeTicket (Comment comment) {
        log.debug("Checking if comment [id={}] should become a ticket. Body: '{}'", comment.getId(), comment.getBody());

        Map<String, Object> requestBody = Map.of(
            "inputs", comment.getBody(),
            "parameters", Map.of("candidate_labels", "this user has a specific technical problem or request")
        );

        log.debug("Request body: {}", requestBody);
        
        ResponseEntity<List> response = restTemplate.postForEntity(deciderUrl, buildEntity(requestBody), List.class);

        List<Map<String, Object>> results = response.getBody();
        String Label = (String) results.get(0).get("label");
        double Score = (Double) results.get(0).get("score");

        log.debug("label: {}, score: {}", Label, Score);

        if (Score >= ticketThreshold){return true;}

        return false;
        
    }

    public List<ExternalTicketData> buildTicketList(Comment comment) {

    String filledPrompt = String.format(PROMPT, comment.getBody());

    Map<String, Object> requestBody = Map.of(
        "model", "Qwen/Qwen3-0.6B:featherless-ai",
        "messages", List.of(
            Map.of("role", "user", "content", filledPrompt)
        )
    );

   ResponseEntity<Map> generatorResponse = restTemplate.postForEntity(
    generatorUrl, buildEntity(requestBody), Map.class
    );
    log.debug("Generator raw response: {}", generatorResponse.getBody());


    List choices = (List) generatorResponse.getBody().get("choices");
    Map message = (Map) ((Map) choices.get(0)).get("message");
    String apiResponse = (String) message.get("content");
    log.debug("Extracted JSON string: {}", apiResponse);

    
    
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
