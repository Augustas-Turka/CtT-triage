package com.example.ctttriage.dto;

import java.util.UUID;

import com.example.ctttriage.model.TicketCategory;
import com.example.ctttriage.model.TicketPriority;

import lombok.Data;

@Data
public class TicketDetailResponse {

    //for GET /tickets/{ticketId} (suggested endpoint), assumed use case- check original comment that triggered the creation of ticket

    private UUID id;//probably not needed since id used in api call
    private String title;
    private TicketCategory category;
    private TicketPriority priority;
    private String summary;
    private CommentResponse sourceComment;

}
