package com.pokotilov.finaltask.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@EqualsAndHashCode
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "adverts")
public class Advert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private LocalDate date;
    private Double price;
    private LocalDate premiumEnd;
    private LocalDate premiumStart;
    private Boolean ban;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @EqualsAndHashCode.Exclude
    private User user;

    @Transient
    private Boolean premium;

    @OneToMany(mappedBy = "advert", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("date ASC")
    @EqualsAndHashCode.Exclude
    private List<Comment> comments = new ArrayList<>();
    @OneToMany(mappedBy = "advert", cascade = CascadeType.ALL, orphanRemoval = true)
    @EqualsAndHashCode.Exclude
    private List<Vote> votes = new ArrayList<>();
    @OneToMany(mappedBy = "advert", cascade = CascadeType.ALL, orphanRemoval = true)
    @EqualsAndHashCode.Exclude
    private List<Chat> chats = new ArrayList<>();

    public Boolean getPremium() {
        if (premium == null) {
            premium = calculatePremiumExpired();
        }
        return premium;
    }

    public Boolean calculatePremiumExpired() {
        return premiumEnd != null && premiumEnd.isAfter(LocalDate.now());
    }
}
