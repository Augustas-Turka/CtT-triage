package com.example.ctttriage.dto;

import lombok.Data;

@Data
public class CommentResponse {

    //for GET comments

    private Long id;
    private String body;
}