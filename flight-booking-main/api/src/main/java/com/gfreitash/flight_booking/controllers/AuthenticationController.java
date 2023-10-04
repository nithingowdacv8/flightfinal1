package com.gfreitash.flight_booking.controllers;

import com.gfreitash.flight_booking.services.dto.input.AuthenticationRequest;
import com.gfreitash.flight_booking.services.dto.input.UserInputDTO;
import com.gfreitash.flight_booking.services.dto.output.AuthenticationResponse;
import com.gfreitash.flight_booking.services.AuthenticationService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authService;

    @PostMapping("/register")
    @Transactional
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody @Valid UserInputDTO userData
    ) {
        return ResponseEntity.ok(authService.register(userData));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody @Valid AuthenticationRequest authenticationRequest
    ) {
        return ResponseEntity.ok(authService.authenticate(authenticationRequest));
    }
}
