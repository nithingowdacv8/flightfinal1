package com.gfreitash.dto_mapper_processor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation used to automatically generate mapper interfaces for DTO classes.
 * This annotation generates a mapper interface that can be used to map data between the DTO and entity classes.
 * It also generates an implementation class using MapStruct so that mapping can be performed at runtime.
 * This annotation is useful for reducing boilerplate code when working with DTOs and entity classes.
 * <p>
 * Example usage:
 * <pre>
 *   &#64;DtoMapper(entity = User.class,
 *          uses = {AddressMapper.class},
 *          imports = {Address.class},
 *          componentModel = "spring",
 *          implementationPackage = "com.example.mapper")
 * </pre>
 *
 * This annotation takes several properties:
 * <ul>
 *   <li>{@code entity}: The entity class that the DTO maps to.</li>
 *   <li>{@code uses}: Optional array of mapper classes to use for complex mapping.</li>
 *   <li>{@code imports}: Optional array of imports to include in the generated mapper interface.</li>
 *   <li>{@code componentModel}: Optional string that specifies which dependency injection model to use (ex. "spring").</li>
 *   <li>{@code implementationPackage}: Optional string that specifies the package name of the generated classes.</li>
 * </ul>
 *
 *
 * @see <a href="https://mapstruct.org">MapStruct</a>
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface DtoMapper {

    Class<?> entity();

    Class<?>[] uses() default {};

    Class<?>[] imports() default {};

    String componentModel() default "default";

    String implementationPackage() default "<PACKAGE_NAME>";

}
