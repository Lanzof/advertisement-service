package com.pokotilov.finaltask.entities;


import jakarta.persistence.*;
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
@Table(name = "votes")
public class Vote {

  @EmbeddedId
  private VoteID voteID;
  private LocalDateTime date;
  private long vote;

  @ManyToOne
  @MapsId("advertId")
  private Advert advert;

  @ManyToOne
  @MapsId("authorId")
  private User author;
}
