package com.epiphany.context;

import org.junit.jupiter.api.*;

import java.util.Optional;

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
        class Binding {

            @Test
            void should_bind_type_to_a_specific_instance() {
                Component instance = new Component() {};
                config.bind(Component.class, instance);
                assertSame(instance, config.context().get(Component.class).get());
            }

            @Test
            void should_return_empty_when_component_not_found() {
                Optional<Component> component = config.context().get(Component.class);
                assertEquals(Optional.empty(), component);
            }

        }

        @Nested
        class DependencyCheck {

            @Test
            void should_not_throw_if_dependencies_are_all_distinct() {
                config.bind(Component.class, ComponentWithInjectConstructor.class);
                config.bind(Dependency.class, DependencyWithInjectConstructor.class);
                config.bind(String.class, "");
                assertDoesNotThrow(() -> config.context());
            }

        }

        @Nested
        class ErrorDependencyCheck {

            @Test
            void should_throw_when_dependency_is_not_found() {
                config.bind(Component.class, ComponentWithInjectConstructor.class);
                DependencyNotFoundException exception = assertThrows(DependencyNotFoundException.class, () -> config.context());
                assertEquals(Dependency.class, exception.dependency());
                assertEquals(Component.class, exception.component());
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

        }

    }

    @Nested
    public class DependenciesSelection {

    }

    @Nested
    public class LifecycleManagement {

    }

}
