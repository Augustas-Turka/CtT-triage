package com.example.ctttriage.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.ctttriage.dto.CommentResponse;
import com.example.ctttriage.dto.CommentReviewRequest;
import com.example.ctttriage.dto.TicketDetailResponse;
import com.example.ctttriage.service.CommentService;

import lombok.RequiredArgsConstructor;



@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping
    public ResponseEntity<List<CommentResponse>> getAll() {
        return ResponseEntity.ok(commentService.getAllComments());
    }

    @GetMapping("/{id}")    
    public ResponseEntity<CommentResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(commentService.getComment(id));
    }
    
    @PostMapping
    public ResponseEntity<List<TicketDetailResponse>> create(@RequestBody CommentReviewRequest request) {
    List<TicketDetailResponse> tickets = commentService.createTickets(request);
    return ResponseEntity.ok(tickets);
}
    
}
