package com.pokotilov.finaltask.util;

import com.pokotilov.finaltask.entities.Advert;
import com.pokotilov.finaltask.entities.User;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class SpecTest {

    @Mock
    private Root<Advert> root;

    @Mock
    private CriteriaQuery<?> query;

    @Mock
    private CriteriaBuilder cb;

    @InjectMocks
    private Spec spec;

    @Test
    public void testToPredicate() {
        // Set up the mock objects
        Join<Advert, User> userJoin = mock(Join.class);
//        when(root.join("user", JoinType.LEFT)).thenReturn(userJoin);

        // Call the toPredicate method
        Predicate predicate = spec.toPredicate(root, query, cb);

        // Verify that the expected methods were called with the expected arguments
        verify(root).join("user", JoinType.LEFT);
        verify(cb).like(cb.lower(root.get("title")), "%" + spec.getTitle().toLowerCase() + "%");
        verify(cb).lessThanOrEqualTo(root.get("price"), spec.getMaxPrice());
        verify(cb).greaterThanOrEqualTo(root.get("price"), spec.getMinPrice());
        verify(cb).greaterThanOrEqualTo(userJoin.get("rating"), spec.getRating());
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/util/specTestData.csv")
    public void testToPredicateWithNullFields(String title, Double maxPrice, Double minPrice,
                                Float rating, String sortField, String sortDirection) {
        // Create a Spec object with the provided values
        Spec spec = new Spec(title, maxPrice, minPrice, rating, sortField, sortDirection);

        // Call the toPredicate method
        Predicate predicate = spec.toPredicate(root, query, cb);

        // Ensure that the expected Predicate object is returned
        if (title == null && minPrice == null && maxPrice == null && rating == null) {
            assertNull(predicate);
        } else {
            assertNotNull(predicate);
        }
    }
}

//class SpecTest {
//
//    @ParameterizedTest
//    @CsvFileSource(resources = "/util/specTestData.csv")
//    public void testToPredicate(String title, Double maxPrice, Double minPrice,
//                                Float rating, String sortField, String sortDirection) {
//        // Arrange
//        Spec spec = new Spec(title, maxPrice, minPrice, rating, sortField, sortDirection);
//
//        Root<Advert> root = mock(Root.class);
//        CriteriaQuery<?> query = mock(CriteriaQuery.class);
//        CriteriaBuilder cb = mock(CriteriaBuilder.class);
//
//        // Act
//        Predicate predicate = spec.toPredicate(root, query, cb);
//
//        // Assert
//        assertNotNull(predicate);
//        assertEquals(4, ((List<Predicate>) predicate).size());
//    }
//}