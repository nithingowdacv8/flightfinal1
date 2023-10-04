package com.gfreitash.flight_booking.controllers.assemblers;

import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.*;
import org.springframework.hateoas.server.RepresentationModelAssembler;

import java.util.function.Function;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class EntityModelAssembler<T> implements RepresentationModelAssembler<T, EntityModel<T>> {

    private final Class<?> controllerClass;

    public EntityModelAssembler(Class<?> controllerClass) {
        this.controllerClass = controllerClass;
    }

    @Override
    @NonNull
    public EntityModel<T> toModel(@NonNull T entity) {
        return EntityModel.of(entity);
    }

    public EntityModel<T> toModel(@NonNull T entity, Link... links) {
        EntityModel<T> entityModel = EntityModel.of(entity);
        entityModel.add(links);
        entityModel.add(linkTo(controllerClass).withRel(IanaLinkRelations.COLLECTION));
        return entityModel;
    }

    //The purpose of this method is to add a generalized way to add links to the collection model,
    //so that the controller doesn't have to do it and the paged model can be appropriately defined with HATEOAS standards
    public CollectionModel<EntityModel<T>> toCollectionModel(
            Iterable<? extends T> entities,
            Function<EntityModel<T>, Void> itemLinks
    ) {
        CollectionModel<EntityModel<T>> collectionModel = RepresentationModelAssembler.super.toCollectionModel(entities);
        collectionModel.forEach(itemLinks::apply);

        return collectionModel;
    }

    public PagedModel<EntityModel<T>> toPagedModel(Page<T> page, Pageable pagination, CollectionModel<EntityModel<T>> collectionModel) {
        var pageMetadata = new PagedModel.PageMetadata(page.getSize(), page.getNumber(), page.getTotalElements());
        PagedModel<EntityModel<T>> pagedModel = PagedModel.of(collectionModel.getContent(), pageMetadata);

        var queryParams = getQueryParams(pagination);
        var link = linkTo(controllerClass);
        var pageParam = "?page=";
        pagedModel.add(link.slash(pageParam + pageMetadata.getNumber() + queryParams).withRel(IanaLinkRelations.SELF));
        pagedModel.add(link.slash(pageParam+"0" + queryParams).withRel(IanaLinkRelations.FIRST));
        pagedModel.add(link.slash(pageParam + (pageMetadata.getTotalPages()-1) + queryParams).withRel(IanaLinkRelations.LAST));

        if (page.hasPrevious())
            pagedModel.add(link.slash(pageParam + (pageMetadata.getNumber()-1) + queryParams).withRel(IanaLinkRelations.PREVIOUS));
        if (page.hasNext())
            pagedModel.add(link.slash(pageParam + (pageMetadata.getNumber()+1) + queryParams).withRel(IanaLinkRelations.NEXT));

        return pagedModel;
    }

    private String getQueryParams(Pageable pagination) {
        var queryParams = new StringBuilder();
        if (pagination.getPageSize() != 20)
            queryParams.append("&size=").append(pagination.getPageSize());

        pagination.getSort().stream().forEach(
                order -> queryParams.append("&sort=").append(order.getProperty())
                        .append(",").append(order.getDirection())
                        .append(order.getNullHandling() != Sort.NullHandling.NATIVE ? ","+order.getNullHandling() : "")
                        .append(order.isIgnoreCase() ? ",ignoreCase" : "")
        );

        return queryParams.toString();
    }
}
