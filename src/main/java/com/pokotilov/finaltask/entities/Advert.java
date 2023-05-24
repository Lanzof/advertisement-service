package com.pokotilov.finaltask.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "adverts")
public class Advert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private String title;
    @NotEmpty
    private String description;
    private LocalDateTime date;
    @NotNull
    @Positive
    private Integer price;
    private Boolean premium;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    private Boolean ban;
    @OneToMany(mappedBy = "advert", cascade = CascadeType.ALL,orphanRemoval = true)
    @OrderBy("date ASC")
    private List<Comment> comments = new ArrayList<>();
    @OneToMany(mappedBy = "advert", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Vote> votes = new ArrayList<>();
    @OneToMany(mappedBy = "advert", cascade = CascadeType.ALL,orphanRemoval = true)
    private Set<Chat> chats = new HashSet<>();
}
