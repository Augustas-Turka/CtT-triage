package com.example.ctttriage.dto;

import com.example.ctttriage.model.TicketCategory;
import com.example.ctttriage.model.TicketPriority;

import lombok.Data;


@Data
public class TicketSummaryResponse {

    // for GET /tickets, use case- requested "Provide an API to view created tickets".  

    private String title;
    private TicketCategory category;
    private TicketPriority priority;
    private String summary;
}
    
