package com.pokotilov.finaltask.repositories;

import com.pokotilov.finaltask.entities.Chat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {

    Optional<Chat> findChatByAdvert_IdAndBuyer_Id(Long advertId, Long buyerId);
    Boolean existsByAdvert_IdAndBuyer_Id(Long advertId, Long buyerId);
    Page<Chat> findChatsByBuyer_IdOrAdvert_User_Id(Long byerId, Long userId, Pageable pageable);
}
