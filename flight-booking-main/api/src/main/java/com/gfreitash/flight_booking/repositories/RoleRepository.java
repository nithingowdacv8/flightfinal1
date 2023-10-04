package com.gfreitash.flight_booking.repositories;

import com.gfreitash.flight_booking.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {

    Optional<Role> findByName(String name);
    boolean existsById(Integer integer);
    boolean existsByName(String name);
}