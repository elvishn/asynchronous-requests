package com.async_requests.demo.controller;

import com.async_requests.demo.service.BankService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
public class Controller {
    private final BankService service;

    public Controller(BankService service) {
        this.service = service;
    }

    @GetMapping
    public Mono<String> getInformation() {
        return service.getInformation();
    }
}
