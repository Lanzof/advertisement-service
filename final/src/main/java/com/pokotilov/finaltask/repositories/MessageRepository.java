package com.pokotilov.finaltask.repositories;

import com.pokotilov.finaltask.entities.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    Page<Message> findAllByChat_Id(Long chatId, Pageable pageable);
}
