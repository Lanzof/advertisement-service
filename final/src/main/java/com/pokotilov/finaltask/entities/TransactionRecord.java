package com.pokotilov.finaltask.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "transactions_history")
public class TransactionRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "wallet_id")
    private Wallet wallet;
    @Enumerated(EnumType.STRING)
    private Operation operation;
    private Double sum;
    private String description;
    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private LocalDateTime date;
}
