package com.example.ctttriage.dto.external;

import com.example.ctttriage.model.TicketCategory;
import com.example.ctttriage.model.TicketPriority;

import lombok.Data;

@Data
public class ExternalTicketData {

    //AI response

    private String title;
    private TicketCategory category;
    private TicketPriority priority;
    private String summary;
}
