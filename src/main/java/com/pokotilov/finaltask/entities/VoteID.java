package com.pokotilov.finaltask.entities;

import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Embeddable
@Data
@Builder
public class VoteID implements Serializable {
    private long authorId;
    private long advertId;
}
