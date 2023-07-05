package com.pokotilov.finaltask.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "wallets")
public class Wallet {
    @Id
    @Column(name = "user_id")
    private Long id;
    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", unique = true, nullable = false, updatable = false)
    @MapsId
    private User user;
    @Column(columnDefinition = "double precision default 0")
    private Double balance;
    @OneToMany(mappedBy = "wallet", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("date DESC")
    private List<TransactionRecord> transactionsHistory = new ArrayList<>();
}
