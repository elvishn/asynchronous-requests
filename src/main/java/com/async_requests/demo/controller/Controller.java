package com.async_requests.demo.controller;

import com.async_requests.demo.service.BankService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
public class Controller {
    private final BankService service;


    public Controller(BankService service) {
        this.service = service;
    }

    @GetMapping
    public Mono<String> getInformation(@RequestHeader(value = "X-Cache-TTL", required = false)
                                           Integer ttl) {
         return service.getInformation(ttl);
    }

    @GetMapping("/{id}")
    public String loadPulling(@PathVariable String id) {
        return service.longPolling(id);
    }
}
