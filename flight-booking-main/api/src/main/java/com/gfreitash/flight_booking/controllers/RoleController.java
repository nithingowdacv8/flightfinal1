package com.gfreitash.flight_booking.controllers;

import com.gfreitash.flight_booking.controllers.assemblers.EntityModelAssembler;
import com.gfreitash.flight_booking.services.dto.input.RoleInputDTO;
import com.gfreitash.flight_booking.services.dto.output.RoleOutputDTO;
import com.gfreitash.flight_booking.services.RoleService;
import com.gfreitash.flight_booking.services.dto.update.RoleUpdateDTO;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.function.Function;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private final RoleService roleService;
    private final EntityModelAssembler<RoleOutputDTO> roleAssembler;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
        this.roleAssembler = new EntityModelAssembler<>(RoleController.class);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<RoleOutputDTO>> getOneRole(@PathVariable Integer id) {
        var selfLink = linkTo(methodOn(RoleController.class).getOneRole(id)).withSelfRel();

        return roleService.getRoleById(id)
                .map(role -> roleAssembler.toModel(role, selfLink))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Transactional
    public ResponseEntity<EntityModel<RoleOutputDTO>> createRole(@RequestBody @Valid RoleInputDTO role) {
        var newRole = roleService.saveRole(role);
        var selfLink = linkTo(methodOn(RoleController.class).getOneRole(newRole.id())).withSelfRel();

        return ResponseEntity.created(selfLink.toUri()).body(roleAssembler.toModel(newRole, selfLink));
    }

    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<RoleOutputDTO>>> getAllRoles(Pageable pagination) {
        var roles = roleService.getAllRoles(pagination);

        Function<EntityModel<RoleOutputDTO>, Void> itemLinks = roleModel -> {
            var roleId = Objects.requireNonNull(roleModel.getContent()).id();

            roleModel.add(linkTo(methodOn(RoleController.class).getOneRole(roleId)).withSelfRel());
            roleService.getRoleByName(roleModel.getContent().parentRole())
                    .ifPresent(
                            parentRole -> roleModel.add(
                                    linkTo(methodOn(RoleController.class)
                                            .getOneRole(parentRole.id()))
                                            .withRel("parentRole")
                            )
                    );

            return null;
        };

        var roleCollectionModel = roleAssembler.toCollectionModel(roles.getContent(),itemLinks);
        var pagedModel = roleAssembler.toPagedModel(roles, pagination, roleCollectionModel);

        return ResponseEntity.ok().body(pagedModel);
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<EntityModel<RoleOutputDTO>> updateRole(@Valid RoleUpdateDTO role) {
        var updatedRole = roleService.updateRole(role);
        return getOneRole(updatedRole.id());
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Object> deleteRole(@PathVariable Integer id) {
        var role = roleService.getRoleById(id);
        if (role.isPresent()) {
            roleService.deleteRole(role.get().id());
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }
}
