package com.pokotilov.finaltask.services.suggest;

import com.kuliginstepan.dadata.client.DadataClient;
import com.kuliginstepan.dadata.client.domain.Suggestion;
import com.kuliginstepan.dadata.client.domain.address.Address;
import com.kuliginstepan.dadata.client.domain.address.AddressRequest;
import com.kuliginstepan.dadata.client.domain.address.AddressRequestBuilder;
import com.kuliginstepan.dadata.client.domain.address.Bound;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class SuggestServiceImpl {

    @Autowired
    private final DadataClient dadataClient;

    public Flux<Suggestion<Address>> getSuggestionsForAddress(String query) {
        AddressRequest request = AddressRequestBuilder
                .create(query)
                .fromBound(Bound.CITY)
                .count(3)
                .build();
        Flux<Suggestion<Address>> suggested = dadataClient.suggestAddress(request);
        return suggested;

    }
}
