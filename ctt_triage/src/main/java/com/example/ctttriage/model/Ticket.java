package com.example.ctttriage.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tickets")
public class Ticket {
 
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private long id;

    @Column(nullable = false, unique = true)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketPriority priority;

    @Column(nullable = false)
    private String summary;

}