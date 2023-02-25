package com.epiphany.context;

import com.epiphany.context.source.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("all")
public class ContainerTest {

    private ContextConfig config;

    @BeforeEach
    void setUp() {
        config = new ContextConfig();
    }

    @Nested
    public class ComponentConstruction {

        @Nested
        class TypeBinding {

            @Test
            void should_bind_type_to_a_specific_instance() {
                Component instance = new Component() {};
                config.bind(Component.class, instance);
                assertSame(instance, config.context().get(Component.class).get());
            }

            @ParameterizedTest(name = "supporting {0}")
            @MethodSource
            void should_bind_type_to_an_injectable_component(Class<? extends Something> componentType) {
                Dependency dependency = new Dependency() {};
                config.bind(Dependency.class, dependency);
                config.bind(Something.class, componentType);
                Optional<Something> something = config.context().get(Something.class);
                assertTrue(something.isPresent());
                assertSame(dependency, something.get().dependency());
            }

            public static Stream<Arguments> should_bind_type_to_an_injectable_component() {
                return Stream.of(
                    Arguments.of(Named.of("Constructor Injection", ConstructorInjection.class)),
                    Arguments.of(Named.of("Field Injection", FieldInjection.class)),
                    Arguments.of(Named.of("Method Injection", MethodInjection.class))
                );
            }

            @Test
            void should_return_empty_when_component_not_found() {
                Optional<Component> component = config.context().get(Component.class);
                assertEquals(Optional.empty(), component);
            }

        }

        @Nested
        class DependencyCheck {

            @ParameterizedTest
            @MethodSource
            void should_throw_exception_if_dependency_not_found(Class<? extends Component> component) {
                config.bind(Component.class, component);
                DependencyNotFoundException exception = assertThrows(DependencyNotFoundException.class, () -> config.context());
                assertEquals(Dependency.class, exception.dependency());
                assertEquals(Component.class, exception.component());
            }

            public static Stream<Arguments> should_throw_exception_if_dependency_not_found() {
                return Stream.of(
                    Arguments.of(Named.of("Inject Constructor", MissingDependencyConstructor.class)),
                    Arguments.of(Named.of("Inject Field", MissingDependencyField.class)),
                    Arguments.of(Named.of("Inject Method", MissingDependencyMethod.class))
                );
            }

            @Test
            void should_throw_if_cyclic_dependencies_found() {
                config.bind(Component.class, ComponentWithInjectConstructor.class);
                config.bind(Dependency.class, DependencyDependedOnComponent.class);
                CyclicDependenciesFoundException exception = assertThrows(CyclicDependenciesFoundException.class, () -> config.context());
                assertEquals(2, exception.components().size());
                assertThat(exception.components()).containsExactlyInAnyOrder(Dependency.class, Component.class);
            }

            @Test
            void should_throw_if_three_cyclic_dependencies_found() {
                config.bind(Component.class, ComponentWithInjectConstructor.class);
                config.bind(Dependency.class, DependencyWithNestedDependency.class);
                config.bind(NestedDependency.class, NestedDependencyOnComponent.class);
                CyclicDependenciesFoundException exception = assertThrows(CyclicDependenciesFoundException.class, () -> config.context());
                assertEquals(3, exception.components().size());
                assertThat(exception.components()).containsExactlyInAnyOrder(Component.class, Dependency.class, NestedDependency.class);
            }

            @Test
            void should_throw_exception_when_field_with_cyclic_dependencies() {
                config.bind(ComponentWithComponentInjection.class, ComponentWithComponentInjection.class);
                assertThrows(CyclicDependenciesFoundException.class, () -> config.context());
            }

            @Test
            void should_not_throw_if_dependencies_are_all_distinct() {
                config.bind(Component.class, ComponentWithInjectConstructor.class);
                config.bind(Dependency.class, DependencyWithInjectConstructor.class);
                config.bind(String.class, "");
                assertDoesNotThrow(() -> config.context());
            }

        }

    }

    @Nested
    public class DependenciesSelection {

    }

    @Nested
    public class LifecycleManagement {

    }

}
