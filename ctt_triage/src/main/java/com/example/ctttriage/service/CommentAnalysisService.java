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
        Priorities: LOW | MEDIUM | HIGH
        Respond ONLY with a valid JSON array. No explanation, no markdown, no code blocks.
        Example format: [{ "title": "...", "category": "BUG", "priority": "HIGH", "summary": "..." }]
        Comment: "%s"
        """;

    private static final String VERIFICATION_PROMPT = """
        You are a strict support ticket reviewer.
        Read the following comment and decide: does it describe a real issue, bug, feature request, billing problem, or account problem that requires a support ticket?
        If it's casual conversation, a compliment, spam, or too vague to act on — it does NOT need a ticket.
        Respond with ONLY one word: YES (if it does require a suuport ticket) or NO.
        Comment: "%s"
        """;

    private final RestTemplate restTemplate = new RestTemplate();


    //using 2 models to save on api costs, first a very cheap zero-shot model, if it passes, we check again with generative model for false positives.
    public boolean shouldCommentBecomeTicket (Comment comment) {

        log.debug("Checking if comment [id={}] should become a ticket. Body: '{}'", comment.getId(), comment.getBody());

        //first check with zero shot model. During testing I found it reliable at detecting when a comment IS NOT a support ticket
        if(!checkWithZeroShotModel(comment.getBody())){
            return false;
        }
        
        //secondary check with generative model. During testing, I found its accuracy to be perfect
        if(checkWithGenerativeModel(comment.getBody())){
            return true;
        }

        return false;
    }


    public List<ExternalTicketData> buildTicketList(Comment comment) {

        String filledPrompt = String.format(PROMPT, comment.getBody());

        Map<String, Object> requestBody = Map.of(
            "model", "Qwen/Qwen2.5-7B-Instruct:together",
            "messages", List.of(
                Map.of("role", "user", "content", filledPrompt)
            )
        );

        //TODO: throw exception if call fails
        ResponseEntity<Map> generatorResponse = restTemplate.postForEntity(
        generatorUrl, buildEntity(requestBody), Map.class
        );
        log.debug("Generator raw response: {}", generatorResponse.getBody());

        List choices = (List) generatorResponse.getBody().get("choices");
        Map message = (Map) ((Map) choices.get(0)).get("message");
        String apiResponse = (String) message.get("content");
        log.debug("Extracted JSON string: {}", apiResponse);

        //TODO: throw exception if json disfigured
        JsonArray jsonArray = JsonParser.parseString(apiResponse).getAsJsonArray();

        List<ExternalTicketData> tickets = new ArrayList<>();
        for (JsonElement element : jsonArray) {
            JsonObject json = element.getAsJsonObject();
            tickets.add(mapJsonResponseToExternalTicketData(json));
        }
        
        return tickets;
    }

    private boolean checkWithZeroShotModel (String text) {
        Map<String, Object> requestBody = Map.of(
            "inputs", text,
            "parameters", Map.of("candidate_labels", "this is an issue or request someone is having with the service")
        );

        log.debug("Request body: {}", requestBody);
        
        //don't add exception throwing here, if this call fails the application will just use the secondary check
        ResponseEntity<List> response = restTemplate.postForEntity(
            deciderUrl, buildEntity(requestBody), List.class
        );

        List<Map<String, Object>> results = response.getBody();
        String Label = (String) results.get(0).get("label");
        double Score = (Double) results.get(0).get("score");

        log.debug("label: {}, score: {}", Label, Score);

        if (Score >= ticketThreshold){return true;}

        return false;
    }

    private boolean checkWithGenerativeModel(String text) {
        String filledPrompt = String.format(VERIFICATION_PROMPT, text);

        Map<String, Object> requestBody = Map.of(
            "model", "Qwen/Qwen2.5-7B-Instruct:together",
            "messages", List.of(
                Map.of("role", "user", "content", filledPrompt)
            )
        );

        //TODO: throw exception if call fails
        ResponseEntity<Map> response = restTemplate.postForEntity(
            generatorUrl, buildEntity(requestBody), Map.class
        );

        //TODO: throw exception if json disfigured
        List choices = (List) response.getBody().get("choices");
        Map message = (Map) ((Map) choices.get(0)).get("message");
        String answer = ((String) message.get("content")).trim().toUpperCase();

        log.debug("Generator verification answer:'{}'",  answer);

        if (answer.equals("YES")){return true;};

        return false;
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
