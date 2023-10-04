package com.gfreitash.flight_booking.services.dto.mappers;

import com.gfreitash.flight_booking.entities.Role;
import com.gfreitash.flight_booking.repositories.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoleMapper {

    private final RoleRepository roleRepository;

    public Role nameStringToRole(String name) {
        return roleRepository.findByName(name).orElse(null);
    }

    public String roleNameToString(Role role) {
        return role != null ? role.getName() : null;
    }

    public Role idToRole(Integer id) {
        return roleRepository.findById(id).orElse(null);
    }
}
