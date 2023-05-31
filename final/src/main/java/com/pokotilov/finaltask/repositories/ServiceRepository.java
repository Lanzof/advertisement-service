package com.pokotilov.finaltask.repositories;

import com.pokotilov.finaltask.entities.PremiumService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceRepository extends JpaRepository<PremiumService, Long> {
}
