package com.gfreitash.flight_booking.services;

import com.gfreitash.flight_booking.services.dto.input.UserInputDTO;
import com.gfreitash.flight_booking.services.dto.output.UserOutputDTO;
import com.gfreitash.flight_booking.services.dto.update.UserUpdateDTO;
import com.gfreitash.flight_booking.entities.User;
import com.gfreitash.flight_booking.repositories.RoleRepository;
import com.gfreitash.flight_booking.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserOutputDTO saveUser(UserInputDTO user) {
        var role = roleRepository.findByName((user.role().name())).orElseThrow();
        var savedRole = userRepository.save(User.builder()
                .email(user.email())
                .name(user.name())
                .surname(user.surname())
                .password(user.password())
                .role(role)
                .build());
        return new UserOutputDTO(savedRole);
    }

    public UserOutputDTO updateUser(String id, UserUpdateDTO user) {
        var userToUpdate = userRepository.findById(Integer.parseInt(id)).orElseThrow();
        userToUpdate.setName(user.name());
        userToUpdate.setSurname(user.surname());
        userToUpdate.setEmail(user.email());
        userToUpdate.setPassword(user.password());
        return new UserOutputDTO(userRepository.save(userToUpdate));
    }

    public User updateUserRole(String id, String role) {
        var userToUpdate = userRepository.findById(Integer.parseInt(id)).orElseThrow();
        var roleToUpdate = roleRepository.findByName(role).orElseThrow();
        userToUpdate.setRole(roleToUpdate);
        return userRepository.save(userToUpdate);
    }

    public Optional<UserOutputDTO> getUserById(String id) {
        return userRepository.findById(Integer.parseInt(id)).map(UserOutputDTO::new);
    }

    public Optional<UserOutputDTO> getUserByEmail(String email) {
        return userRepository.findByEmail(email).map(UserOutputDTO::new);
    }

    public List<UserOutputDTO> getAllUsers() {
        return userRepository.findAll().stream().map(UserOutputDTO::new).toList();
    }

    public Page<UserOutputDTO> getAllUsers(Pageable pagination) {
        return userRepository.findAll(pagination).map(UserOutputDTO::new);
    }

    public void deleteUser(Integer id) {
        userRepository.deleteById(id);
    }
}
