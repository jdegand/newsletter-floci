package com.example.newsletterapi.api;

import com.example.newsletterapi.service.SubscriptionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class SubscribeController {

    private final SubscriptionService subscriptionService;

    public SubscribeController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @PostMapping("/subscribe")
    public ResponseEntity<Void> subscribe(@Valid @RequestBody SubscribeRequest request) {
        subscriptionService.subscribe(request.getEmail());
        return ResponseEntity.ok().build();
    }
}
