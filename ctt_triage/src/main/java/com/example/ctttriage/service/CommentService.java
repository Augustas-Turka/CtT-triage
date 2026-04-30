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

    // public List<Ticket> createTickets(Comment comment) {
    //     //api call, give comment, get json

    //     //response? if no ticket, return, if yes- get json

    //     //mapping call, iterate over json and save each ticket
    // }

//mapping methods

    private CommentResponse mapCommentToCommentResponse(Comment comment) {

        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        response.setBody(comment.getBody());
        return response;
    }
    
}
