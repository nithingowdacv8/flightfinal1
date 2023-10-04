package com.gfreitash.flight_booking.controllers.assemblers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.mediatype.MessageResolver;
import org.springframework.hateoas.mediatype.hal.DefaultCurieProvider;
import org.springframework.hateoas.mediatype.hal.Jackson2HalModule;
import org.springframework.hateoas.server.core.DefaultLinkRelationProvider;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@SpringBootTest
class EntityModelAssemblerTest {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    private static class TestClass {
        private long id;
        private String name;
    }

    @RestController
    @RequestMapping("/api/test")
    private static class TestController {}

    private EntityModelAssembler<TestClass> entityModelAssembler;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        entityModelAssembler = new EntityModelAssembler<>(TestController.class);

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new Jackson2HalModule());

        var relProvider = new DefaultLinkRelationProvider();
        var curieProvider = DefaultCurieProvider.NONE;
        var messageResolver = MessageResolver.DEFAULTS_ONLY;
        objectMapper.setHandlerInstantiator(new Jackson2HalModule.HalHandlerInstantiator(relProvider, curieProvider, messageResolver));
    }

    @Test
    @DisplayName("toModel(entity) should return a json representation of the model (without any HAL links)")
    void toModelTest1() throws JsonProcessingException {
        var id = 1L;
        var name = "test";

        var testClass = TestClass.builder().id(id).name(name).build();
        var model = entityModelAssembler.toModel(testClass);
        var jsonNode = objectMapper.readTree(objectMapper.writeValueAsString(model));

        var nameProperty = jsonNode.get("name");
        Assertions.assertThat(nameProperty.asText()).isEqualTo(name);

        var idProperty = jsonNode.get("id");
        Assertions.assertThat(idProperty.asLong()).isEqualTo(id);
    }

    @Test
    @DisplayName("toModel(entity, ...links) should return a hal+json representation of the model with links passed and the collection link")
    void toModelTest2() throws JsonProcessingException {
        var id = 1L;
        var name = "test";
        var selfLink = linkTo(TestController.class).slash(id).withSelfRel();
        var collectionLink = linkTo(TestController.class).withRel("collection");

        var testClass = TestClass.builder().id(id).name(name).build();
        var model = entityModelAssembler.toModel(testClass, selfLink);
        var jsonNode = objectMapper.readTree(objectMapper.writeValueAsString(model));

        var selfLinkNode = jsonNode.get("_links").get("self").get("href");
        Assertions.assertThat(selfLinkNode.asText()).isEqualTo(selfLink.getHref());

        var collectionLinkNode = jsonNode.get("_links").get("collection").get("href");
        Assertions.assertThat(collectionLinkNode.asText()).isEqualTo(collectionLink.getHref());


        var nameProperty = jsonNode.get("name");
        Assertions.assertThat(nameProperty.asText()).isEqualTo(name);

        var idProperty = jsonNode.get("id");
        Assertions.assertThat(idProperty.asLong()).isEqualTo(id);
    }

    @Test
    @DisplayName("toModel(entity) should return NullPointException when entity is null")
    void toModelTest3() {
        Assertions.assertThatThrownBy(() -> entityModelAssembler.toModel(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("toCollectionModel(entities) should return a hal+json representation of the collection of entities")
    void toCollectionModelTest1 () throws JsonProcessingException {
        var id1 = 1L;
        var name1 = "test1";
        var id2 = 2L;
        var name2 = "test2";

        var testClass1 = TestClass.builder().id(id1).name(name1).build();
        var testClass2 = TestClass.builder().id(id2).name(name2).build();

        var collectionModel = entityModelAssembler.toCollectionModel(Arrays.asList(testClass1, testClass2));
        var jsonNode = objectMapper.readTree(objectMapper.writeValueAsString(collectionModel));
        var embeddedNode = jsonNode.get("_embedded").get("testClassList");

        var testClassList = objectMapper.readValue(embeddedNode.toPrettyString(), new TypeReference<TestClass[]>() {});
        Assertions.assertThat(testClassList).containsExactlyInAnyOrder(testClass1, testClass2);
    }

    @Test
    @DisplayName("toPagedModel(entities) should return a hal+json representation of the collection with expected pagination links")
    void toPagedModelTest1() throws JsonProcessingException {
        final var totalElements = 100;
        final var pageSize = 10;
        final int lastPageNumber = (int) Math.ceil(((double) totalElements / pageSize) - 1);
        final var pageRequestNumber = 2;
        final var elementsList = new ArrayList<TestClass>(totalElements);

        for (int i = 0; i < totalElements; i++) {
            elementsList.add(TestClass.builder().id(i).name("test" + i).build());
        }

        var order = new Sort.Order(Sort.Direction.DESC, "id");
        var pageElements = elementsList.stream().sorted(Comparator.comparing(TestClass::getId).reversed()).skip(pageRequestNumber * pageSize).limit(pageSize).toList();
        var pageRequest = PageRequest.of(pageRequestNumber, pageSize, Sort.by(order));
        var page = new PageImpl<>(pageElements, pageRequest, totalElements);
        var queryParameters = "&size=" + pageSize + "&sort=" + order.getProperty() + "," + order.getDirection();

        Function<EntityModel<TestClass>, Void> links = entityModel -> {
            entityModel.add(linkTo(TestController.class).slash(entityModel.getContent().getId()).withSelfRel());
            return null;
        };

        var pagedModel = entityModelAssembler.toPagedModel(page, pageRequest, entityModelAssembler.toCollectionModel(page, links));
        var jsonNode = objectMapper.readTree(objectMapper.writeValueAsString(pagedModel));
        var embeddedNode = jsonNode.get("_embedded").get("testClassList");

        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        var testClassList = objectMapper.readValue(embeddedNode.toPrettyString(), new TypeReference<List<TestClass>>() {});
        Assertions.assertThat(testClassList).containsExactlyElementsOf(pageElements);

        var collectionLinks = jsonNode.get("_links");
        var selfLink = collectionLinks.get("self").get("href");
        Assertions.assertThat(selfLink.asText()).isEqualTo(linkTo(TestController.class).toUri() + "?page=" + pageRequestNumber + queryParameters);

        var firstLink = collectionLinks.get("first").get("href");
        Assertions.assertThat(firstLink.asText()).isEqualTo(linkTo(TestController.class).toUri() + "?page=0" + queryParameters);

        var lastLink = collectionLinks.get("last").get("href");
        Assertions.assertThat(lastLink.asText()).isEqualTo(linkTo(TestController.class).toUri() + "?page=" + lastPageNumber + queryParameters);

        var nextLink = collectionLinks.get("next").get("href");
        Assertions.assertThat(nextLink.asText()).isEqualTo(linkTo(TestController.class).toUri() + "?page=" + (pageRequestNumber + 1) + queryParameters);

        var previousLink = collectionLinks.get("previous").get("href");
        Assertions.assertThat(previousLink.asText()).isEqualTo(linkTo(TestController.class).toUri() + "?page=" + (pageRequestNumber - 1) + queryParameters);

        var pageProperty = jsonNode.get("page");
        Assertions.assertThat(pageProperty.get("size").asInt()).isEqualTo(pageSize);
        Assertions.assertThat(pageProperty.get("totalElements").asInt()).isEqualTo(totalElements);
        Assertions.assertThat(pageProperty.get("totalPages").asInt()).isEqualTo(lastPageNumber + 1);
        Assertions.assertThat(pageProperty.get("number").asInt()).isEqualTo(pageRequestNumber);
    }
}
