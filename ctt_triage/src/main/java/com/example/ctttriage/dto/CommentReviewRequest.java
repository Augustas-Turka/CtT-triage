package com.example.ctttriage.dto;

import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommentReviewRequest {

    // for POST /comment

    //TODO: verify message size constraints with client. e.g. one char messages definitely arent a ticket, might save api costs
    // @Size(min = MIN, max = MAX, message = "Comment must be between MIN and MAX characters")
    @NotBlank(message = "Comment body is required")
    private String body;

}