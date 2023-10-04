package com.gfreitash.dto_mapper_processor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.*;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

/**
 * A custom annotation processor that generates mapper interfaces and implementation classes for DTOs.
 * This processor is triggered by the `@DtoMapper` annotation and uses MapStruct to generate the implementation classes.
 * The generated classes are based on the properties specified in the `@DtoMapper` annotation.
 */
@AutoService(Processor.class)
@SupportedAnnotationTypes("com.gfreitash.dto_mapper_processor.DtoMapper")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class DtoMapperProcessor extends AbstractProcessor {
    private Filer filer;
    private Messager messager;

    private static final EnumSet<ElementKind> DTO_TYPES = EnumSet.of(ElementKind.CLASS, ElementKind.RECORD);
    private static final EnumSet<ElementKind> DTO_FIELDS = EnumSet.of(ElementKind.FIELD, ElementKind.RECORD_COMPONENT);
    private static final String ENTITY_FIELD_NAME = "entity";

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.filer = processingEnv.getFiler();
        this.messager = processingEnv.getMessager();
    }

    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(DtoMapper.class)) {
            if (!DTO_TYPES.contains(annotatedElement.getKind()) || getEntityElement(annotatedElement) == null) {
                continue;
            }

            var typeElement = (TypeElement) annotatedElement;
            var annotationPackage = getAnnotationValue(getAnnotationMirror(annotatedElement).orElseThrow(), "implementationPackage");
            var packageName = annotationPackage.isPresent() ? annotationPackage.get().getValue().toString()
                    : processingEnv.getElementUtils().getPackageOf(typeElement).getQualifiedName().toString();

            var mapperAnnotationBuilder = AnnotationSpec.builder(Mapper.class);
            fillMapperAnnotation(annotatedElement, mapperAnnotationBuilder);

            var className = typeElement.getSimpleName().toString() + "Mapper";
            var builder = TypeSpec
                    .interfaceBuilder(className)
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                    .addAnnotation(mapperAnnotationBuilder.build());

            processFields(annotatedElement, builder);
            addInstanceConstant(packageName, className, typeElement, builder);

            var javaFile = JavaFile.builder(packageName, builder.build()).build();
            try {
                javaFile.writeTo(filer);
            } catch (IOException e) {
                messager.printMessage(Diagnostic.Kind.WARNING, e.getMessage(), annotatedElement);
            }
        }
        return true;
    }

    /**
     * Fills the mapperAnnotationBuilder with the values from the @DtoMapper annotation.
     * It will copy all annotation values except the "entity" field from @DtoMapper to the @Mapper annotation.
     *
     * @param annotatedElement the element annotated with @DtoMapper
     * @param mapperAnnotationBuilder the builder for creating the @Mapper annotation
     */
    private void fillMapperAnnotation(Element annotatedElement, AnnotationSpec.Builder mapperAnnotationBuilder) {
        var annotation = getAnnotationMirror(annotatedElement).orElseThrow();

        final var elementUtils = this.processingEnv.getElementUtils();
        final var elementValues = elementUtils.getElementValuesWithDefaults(annotation);

        elementValues.entrySet().stream()
                .filter(entry -> !entry.getKey().getSimpleName().toString().equals(ENTITY_FIELD_NAME))
                .forEach(entry -> mapperAnnotationBuilder.addMember(entry.getKey().getSimpleName().toString(), "$L", entry.getValue()));
    }

    /**
     * Processes the fields of the annotated element and adds the mapping methods to the type builder.
     * Uses the MapStruct {@code @Mapping} annotation to generate the mappings.
     * <br>
     * It creates two methods:
     * <ul>
     *     <li>{@code toDto} - maps from entity to DTO</li>
     *     <li>{@code toEntity} - maps from DTO to entity</li>
     *     <li>The {@code toEntity} will have a {@code @Mapping} annotation for each field of the DTO that is annotated with {@code @Mapping}</li>
     *     <li>The {@code toDto} will have a {@code @InheritInverseConfiguration} related to the {@code toEntity} method</li>
     *     <li>The {@code @Mapping} annotation will have the same values as the {@code @Mapping} annotation on the DTO field</li>
     * </ul>
     *
     * @param annotatedElement the element annotated with {@code @DtoMapper}
     * @param builder the builder for the generated mapper interface
     */
    private void processFields(Element annotatedElement, TypeSpec.Builder builder) {
        var annotationValue = getEntityElement(annotatedElement);
        assert annotationValue != null;

        var dtoClassName = TypeName.get(annotatedElement.asType());
        var entityClassName = TypeName.get(annotationValue.asType());

        var toDtoMethod = MethodSpec.methodBuilder("toDto")
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .returns(dtoClassName)
                .addParameter(entityClassName, "e");

        var toEntityMethod = MethodSpec.methodBuilder("toEntity")
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .returns(entityClassName)
                .addParameter(dtoClassName, "d");

        for (var enclosedElement : annotatedElement.getEnclosedElements()) {
            if (DTO_FIELDS.contains(enclosedElement.getKind())) {
                addMappingAnnotationField(enclosedElement, toEntityMethod);
            }
        }

        var inheritInverseConfiguration = AnnotationSpec.builder(InheritInverseConfiguration.class)
                .addMember("name", "$S", "toEntity")
                .build();
        toDtoMethod.addAnnotation(inheritInverseConfiguration);

        builder.addMethod(toEntityMethod.build());
        builder.addMethod(toDtoMethod.build());
    }
    /**
     * Adds the {@code @Mapping} annotations from a given field to the given {@code toEntityMethod} builder.
     * This method iterates through the annotations on the field and copies any {@code @Mapping} annotations found
     * to the specified method builder.
     *
     * @param field The field element containing the {@code @Mapping} annotations to be copied.
     * @param toEntityMethod The method builder for the "toEntity" method, where the {@code @Mapping} annotations will be added.
     */
    private void addMappingAnnotationField(Element field, MethodSpec.Builder toEntityMethod) {
        for (AnnotationMirror mirror : field.getAnnotationMirrors()) {
            if (mirror.getAnnotationType().toString().equals(Mapping.class.getName())) {
                toEntityMethod.addAnnotation(AnnotationSpec.get(mirror));
            }
        }
    }

    /**
     * Adds an INSTANCE constant field to the mapper interface.
     * The INSTANCE constant will hold the reference to the singleton instance of the mapper implementation generated by MapStruct.
     *
     * @param packageName  the package name of the generated mapper interface
     * @param className    the name of the generated mapper interface class
     * @param typeElement  the annotated DTO element
     * @param builder      the builder for the generated mapper interface
     */
    private void addInstanceConstant(String packageName, String className, TypeElement typeElement, TypeSpec.Builder builder) {
        builder.addField(FieldSpec.builder(ClassName.get(packageName, className), "INSTANCE", Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .initializer(
                        "$T.getMapper($T.class)",
                        Mappers.class,
                        ClassName.get(packageName, typeElement.getSimpleName().toString().concat("Mapper")))
                .build());
    }

    /**
     * Retrieves the entity element associated with the given annotated element.
     * The entity element is specified in the @DtoMapper annotation's "entity" field.
     *
     * @param element The element that has been annotated with @DtoMapper.
     * @return The element representing the entity associated with the given annotated element.
     */
    private Element getEntityElement(Element element) {
        var annotation = getAnnotationMirror(element).orElseThrow();
        var annotationValue = getAnnotationValue(annotation, ENTITY_FIELD_NAME).orElseThrow();

        return this.processingEnv.getTypeUtils().asElement((TypeMirror) annotationValue.getValue());
    }

    /**
     * Searches for and returns the @DtoMapper annotation mirror for the given element.
     *
     * @param element The element that has been annotated with @DtoMapper.
     * @return An Optional containing the annotation mirror for the @DtoMapper annotation if present, otherwise an empty Optional.
     */
    private Optional<? extends AnnotationMirror> getAnnotationMirror(Element element) {
        return element.getAnnotationMirrors().stream()
                .filter(mirror -> mirror.getAnnotationType().toString().equals(DtoMapper.class.getName()))
                .findFirst();
    }

    /**
     * Retrieves the value associated with the given element name for the specified annotation mirror.
     *
     * @param annotation The annotation mirror containing the desired element value.
     * @param elementName The name of the element whose value needs to be retrieved.
     * @return An Optional containing the annotation value for the specified element name if present, otherwise an empty Optional.
     */
    private Optional<? extends AnnotationValue> getAnnotationValue(AnnotationMirror annotation, String elementName) {
        final var elementUtils = this.processingEnv.getElementUtils();
        final var elementValues = elementUtils.getElementValuesWithDefaults(annotation);

        return elementValues.keySet().stream()
                .filter(key -> key.getSimpleName().toString().equals(elementName))
                .map(elementValues::get)
                .findAny();
    }

}
