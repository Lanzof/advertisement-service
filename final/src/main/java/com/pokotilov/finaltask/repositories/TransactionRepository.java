package com.pokotilov.finaltask.repositories;

import com.pokotilov.finaltask.entities.TransactionRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionRecord, Long> {
}
