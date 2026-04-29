package com.example.ctttriage.dto;

import java.util.UUID;

import lombok.Data;

@Data
public class CommentResponse {

    //for GET comments

    private UUID id;
    private String body;
}