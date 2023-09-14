package com.pokotilov.finaltask.controllers;

import com.kuliginstepan.dadata.client.domain.Suggestion;
import com.kuliginstepan.dadata.client.domain.address.Address;
import com.pokotilov.finaltask.services.suggest.SuggestServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
public class SuggestingController {

    @Autowired
    private final SuggestServiceImpl suggestService;

    @GetMapping("/suggest")
    public Flux<Suggestion<Address>> suggestAddress(String request) {
        return suggestService.getSuggestionsForAddress(request);
    }
}
