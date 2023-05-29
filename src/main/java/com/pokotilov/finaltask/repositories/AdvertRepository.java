package com.pokotilov.finaltask.repositories;

import com.pokotilov.finaltask.entities.Advert;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AdvertRepository extends JpaRepository<Advert, Long> {
    Page<Advert> getAdvertsByBanFalseAndAndUser_BanFalse(Pageable pageable);
    @Query(value = "SELECT a.* FROM public.adverts a " +
            "LEFT JOIN users u ON u.id = a.user_id " +
            "WHERE a.ban = false AND u.ban = false " +
            "ORDER BY a.premium_end > now() DESC",
            countQuery = "SELECT count(*) FROM public.adverts a " +
                    "LEFT JOIN users u ON u.id = a.user_id WHERE a.ban = false AND u.ban = false",
            nativeQuery = true)
    Page<Advert> getAdvertsByDefaultQuery(Pageable pageable);

    @Query(value = "SELECT a.* FROM public.adverts a " +
            "LEFT JOIN users u ON u.id = a.user_id " +
            "WHERE a.ban = false AND u.ban = false " +
            "ORDER BY a.premium_end > now() DESC",
            countQuery = "SELECT count(*) FROM public.adverts a " +
                    "LEFT JOIN users u ON u.id = a.user_id WHERE a.ban = false AND u.ban = false",
            nativeQuery = true)
    Page<Advert> findAdvertsByTitleAndFilters(Example<Advert> example, Pageable pageable);
}

//    @Query(value = "SELECT a.* FROM public.adverts a " +
//            "LEFT JOIN users u ON u.id = a.user_id " +
//            "WHERE (lower(a.title) ilike lower('%' || :title || '%'))",// +
//            "AND a.ban = false AND u.ban = false " +
//            "AND u.rating >= :rating OR :rating IS NULL " +
//            "AND a.price >= :priceMin OR :priceMin IS NULL " +
//            "AND a.price <= :priceMax OR :priceMax IS NULL " +
//            "ORDER BY a.premium_end > now() DESC",
//            countQuery = "SELECT count(*) FROM public.adverts a " +
//                    "LEFT JOIN users u ON u.id = a.user_id " +
//                    "WHERE a.ban = false AND u.ban = false " +
//                    "AND a.title ILIKE ('%' || :title || '%') " +
//                    "AND :rating IS NULL OR u.rating >= :rating " +
//                    "AND :priceMin IS NULL OR a.price >= :priceMin " +
//                    "AND :priceMax IS NULL OR a.price <= :priceMax ",
//            nativeQuery = true)