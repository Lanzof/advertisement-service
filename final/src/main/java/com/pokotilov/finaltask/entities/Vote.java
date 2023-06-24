package com.pokotilov.finaltask.entities;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "votes")
public class Vote {

    @EmbeddedId
    private VoteID voteID;
    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private LocalDateTime date;
    private Integer vote;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("advertId")
    private Advert advert;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("authorId")
    private User author;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vote vote = (Vote) o;
        return Objects.equals(voteID, vote.voteID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(voteID);
    }
}
