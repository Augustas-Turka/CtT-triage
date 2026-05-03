package com.example.ctttriage.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.ctttriage.dto.CommentResponse;
import com.example.ctttriage.dto.CommentReviewRequest;
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
    public ResponseEntity<CommentResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(commentService.getComment(id));
    }
    
    @PostMapping //TODO: decide on response- return created tickets? boolean?
    public ResponseEntity<?> create(@RequestBody CommentReviewRequest request) {
        commentService.createTickets(request);//void at the moment, will be changed to bool/responseentity
        return (ResponseEntity.ok(true));
    }
    
}
