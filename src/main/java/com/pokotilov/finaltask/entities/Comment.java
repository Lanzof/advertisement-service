package com.pokotilov.finaltask.entities;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "advert_id")
    @NotEmpty
    private Advert advert;
    @ManyToOne
    @JoinColumn(name = "user_id")
    @NotEmpty
    private User author;
    private LocalDateTime date;
    private String text;
    private Boolean ban;
}
