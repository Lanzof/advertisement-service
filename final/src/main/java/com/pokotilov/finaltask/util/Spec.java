package com.pokotilov.finaltask.util;

import com.pokotilov.finaltask.entities.Advert;
import com.pokotilov.finaltask.entities.User;
import jakarta.persistence.criteria.*;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Builder
@Data
public class Spec implements Specification<Advert> {

    private String title;
    private Double maxPrice;
    private Double minPrice;
    private Float rating;
    private String sortField;
    private String sortDirection;

    @Override
    public Predicate toPredicate(Root<Advert> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        Join<Advert, User> userJoin = root.join("user", JoinType.LEFT);

        Predicate titleSearch = Optional.ofNullable(title).map(s -> cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%")).orElse(null);
        Predicate maxPricePred = Optional.ofNullable(maxPrice).map(s -> cb.lessThanOrEqualTo(root.get("price"), maxPrice)).orElse(null);
        Predicate minPricePred = Optional.ofNullable(minPrice).map(s -> cb.greaterThanOrEqualTo(root.get("price"), minPrice)).orElse(null);
        Predicate ratingPred = Optional.ofNullable(rating).map(s -> cb.greaterThanOrEqualTo(userJoin.get("rating"), rating)).orElse(null);

        List<Predicate> predicates = new ArrayList<>();

        Optional.ofNullable(titleSearch).ifPresent(predicates::add);
        Optional.ofNullable(maxPricePred).ifPresent(predicates::add);
        Optional.ofNullable(minPricePred).ifPresent(predicates::add);
        Optional.ofNullable(ratingPred).ifPresent(predicates::add);

        if (sortField == null) {
            sortField = "rating";
        }
        if (sortDirection == null) {
            sortDirection = "desc";
        }

        List<Order> orders = new ArrayList<>();

        Order premiumOrder = cb.desc(cb.greaterThanOrEqualTo(root.get("premiumEnd"), LocalDate.now()));
        Order sortOrder;

        if (sortField.equals("rating")) {
            sortOrder = sortDirection.equals("asc")
                    ? cb.asc(userJoin.get(sortField))
                    : cb.desc(userJoin.get(sortField));
        } else {
            sortOrder = sortDirection.equals("asc")
                    ? cb.asc(root.get(sortField.toLowerCase()))
                    : cb.desc(root.get(sortField.toLowerCase()));
        }

        orders.add(premiumOrder);
        orders.add(sortOrder);

        query.orderBy(orders);
        return cb.and(predicates.toArray(new Predicate[predicates.size()]));
    }
}
