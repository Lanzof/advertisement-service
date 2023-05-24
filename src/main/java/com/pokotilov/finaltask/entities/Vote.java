package com.pokotilov.finaltask.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

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
  @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm:ss")
  private LocalDateTime date;
  private long vote;

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("advertId")
  private Advert advert;

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("authorId")
  private User author;
}
