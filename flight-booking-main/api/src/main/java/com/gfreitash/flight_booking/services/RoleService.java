package com.gfreitash.flight_booking.services;

import com.gfreitash.flight_booking.config.ValidationsConfig;
import com.gfreitash.flight_booking.repositories.RoleRepository;
import com.gfreitash.flight_booking.services.dto.input.RoleInputDTO;
import com.gfreitash.flight_booking.services.dto.mappers.RoleInputDTOMapper;
import com.gfreitash.flight_booking.services.dto.mappers.RoleOutputDTOMapper;
import com.gfreitash.flight_booking.services.dto.output.RoleOutputDTO;
import com.gfreitash.flight_booking.services.dto.update.RoleUpdateDTO;
import com.gfreitash.flight_booking.services.validations.SpecificationValidator;
import com.gfreitash.flight_booking.services.validations.exceptions.RoleDoesNotExistException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final ValidationsConfig validationsConfig;
    private final RoleInputDTOMapper roleInputDTOMapper;
    private final RoleOutputDTOMapper roleOutputDTOMapper;

    public RoleOutputDTO saveRole(RoleInputDTO role) {
        List<SpecificationValidator<RoleInputDTO>> validators = validationsConfig.getSpecificationValidators(RoleInputDTO.class);
        validators.forEach(validator -> validator.validate(role));

        var savedRole = roleRepository.save(roleInputDTOMapper.toEntity(role));
        return roleOutputDTOMapper.toDto(savedRole);
    }

    public RoleOutputDTO updateRole(RoleUpdateDTO role) {
        List<SpecificationValidator<RoleUpdateDTO>> roleInputDtoValidators = validationsConfig.getSpecificationValidators(RoleUpdateDTO.class);
        roleInputDtoValidators.forEach(validator -> validator.validate(role));

        var roleToUpdate = roleRepository.findById(role.id()).orElseThrow(()->new RoleDoesNotExistException("Role does not exist"));
        roleToUpdate.setName(role.name());
        roleToUpdate.setParentRole(roleRepository.findByName(role.parentRole()).orElse(null));

        return roleOutputDTOMapper.toDto(roleRepository.save(roleToUpdate));
    }

    public Optional<RoleOutputDTO> getRoleById(Integer id) {
        return roleRepository.findById(id).map(roleOutputDTOMapper::toDto);
    }

    public Optional<RoleOutputDTO> getRoleByName(String name) {
        return roleRepository.findByName(name).map(roleOutputDTOMapper::toDto);
    }

    public List<RoleOutputDTO> getAllRoles() {
        return roleRepository.findAll().stream().map(roleOutputDTOMapper::toDto).toList();
    }

    public Page<RoleOutputDTO> getAllRoles(Pageable pagination) {
        return roleRepository.findAll(pagination).map(roleOutputDTOMapper::toDto);
    }

    public void deleteRole(Integer id) {
        roleRepository.deleteById(id);
    }
}
