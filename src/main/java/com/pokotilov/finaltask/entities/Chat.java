package com.pokotilov.finaltask.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "chats")
public class Chat {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "advert_id")
  private Advert advert;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "buyer_id")
  private User buyer;

  @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL,orphanRemoval = true)
  @OrderBy("date ASC")
  private List<Message> messageList = new ArrayList<>();

}
