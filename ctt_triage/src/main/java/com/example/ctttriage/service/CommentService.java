package com.example.ctttriage.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.ctttriage.repositories.CommentRepository;
import com.example.ctttriage.model.*;//all for now
import com.example.ctttriage.dto.*;//all objects for now


import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    public List<CommentResponse> getAllComments () {

        return commentRepository.findAll().stream().map(this::mapCommentToCommentResponse).toList();
    }

//mapping methods

    private CommentResponse mapCommentToCommentResponse(Comment comment) {

        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        response.setBody(comment.getBody());
        return response;
    }
    
}
