package com.pokotilov.finaltask.entities;

import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Embeddable
@Data
@Builder
public class VoteID implements Serializable {
    @ManyToOne
    @JoinColumn(name = "advert_id")
    private Advert advert;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;
}
