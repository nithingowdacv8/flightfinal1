package com.gfreitash.flight_booking.services;

import com.gfreitash.flight_booking.services.dto.input.AuthenticationRequest;
import com.gfreitash.flight_booking.services.dto.input.UserInputDTO;
import com.gfreitash.flight_booking.services.dto.output.AuthenticationResponse;
import com.gfreitash.flight_booking.entities.User;
import com.gfreitash.flight_booking.services.validations.exceptions.EmailAlreadyRegisteredException;
import com.gfreitash.flight_booking.services.validations.exceptions.RoleDoesNotExistException;
import com.gfreitash.flight_booking.repositories.RoleRepository;
import com.gfreitash.flight_booking.repositories.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository repository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;
    private final JwtService jwt;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthenticationResponse register(@RequestBody @Valid UserInputDTO userData) {
        repository.findByEmail(userData.email()).ifPresent(
                user -> {throw new EmailAlreadyRegisteredException(
                        String.format("Email %s is already registered", userData.email())
                );}
        );

        var user = User.builder()
                .name(userData.name())
                .surname(userData.surname())
                .email(userData.email())
                .password(encoder.encode(userData.password()))
                .role(roleRepository.findByName(userData.role().name())
                        .orElseThrow(() -> new RoleDoesNotExistException(
                                String.format("Role %s does not exist", userData.role().name())
                        ))
                )
                .registrationDate(LocalDateTime.now())
                .build();

        repository.save(user);
        var jwtToken = jwt.generateToken(user, Map.of("role", user.getRole()));
        return new AuthenticationResponse(jwtToken);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.email(),
                        authenticationRequest.password()
                )
        );

        var user = repository.findByEmail(authenticationRequest.email())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));
        var jwtToken = jwt.generateToken(user, Map.of("role", user.getRole()));
        return new AuthenticationResponse(jwtToken);
    }
}
