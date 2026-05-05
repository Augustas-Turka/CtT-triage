package com.example.ctttriage.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@Entity
@Table(name = "tickets")
public class Ticket {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne//In case multiple tickets originate form one comment
    @JoinColumn(name = "source_comments_id", nullable = false)
    private Comment sourceComment;

    @Column(nullable = false, unique = true)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketPriority priority;

    @Column(nullable = false, columnDefinition = "TEXT")//max summary length?
    private String summary;

}