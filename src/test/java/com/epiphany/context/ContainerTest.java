package com.epiphany.context;

import com.epiphany.context.exception.*;
import com.epiphany.context.source.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.util.*;
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
            void should_bind_type_from_class_path() {
                config.bind(Dependency.class, new ClassName("com.epiphany.context.source.DependencyWithNestedDependency"));
                config.bind(NestedDependency.class, new NestedDependency() {});
                Optional<Dependency> dependency = config.context().get(Dependency.class);
                assertTrue(dependency.isPresent());
                assertThat(dependency.get()).isInstanceOf(DependencyWithNestedDependency.class);
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

            @ParameterizedTest(name = "cyclic dependency between {0} and {1}")
            @MethodSource
            void should_throw_exception_if_cyclic_dependencies_found(Class<? extends Component> component, Class<? extends Dependency> dependency) {
                config.bind(Component.class, component);
                config.bind(Dependency.class, dependency);
                CyclicDependenciesFoundException exception = assertThrows(CyclicDependenciesFoundException.class, () -> config.context());
                Set<Class<?>> classes = exception.components();
                assertEquals(2, classes.size());
                assertTrue(classes.contains(Component.class));
                assertTrue(classes.contains(Dependency.class));
            }

            public static Stream<Arguments> should_throw_exception_if_cyclic_dependencies_found() {
                List<Arguments> arguments = new ArrayList<>();
                for (Named component : List.of(
                    Named.of("Inject Constructor", CyclicComponentInjectConstructor.class),
                    Named.of("Inject Field", CyclicComponentInjectField.class),
                    Named.of("Inject Method", CyclicComponentInjectMethod.class))
                ) {
                    for (Named dependency : List.of(
                        Named.of("Inject Constructor", CyclicDependencyInjectMethod.class),
                        Named.of("Inject Field", CyclicDependencyInjectField.class),
                        Named.of("Inject Method", CyclicDependencyInjectMethod.class)
                    )) {
                        arguments.add(Arguments.of(component, dependency));
                    }
                }
                return arguments.stream();
            }

            @ParameterizedTest(name = "indirect cyclic dependency between {0}, {1} and {2}")
            @MethodSource
            void should_throw_exception_if_transitive_cyclic_dependencies_found(Class<? extends Component> component, Class<? extends Dependency> dependency, Class<? extends AnotherDependency> anotherDependency) {
                config.bind(Component.class, component);
                config.bind(Dependency.class, dependency);
                config.bind(AnotherDependency.class, anotherDependency);
                CyclicDependenciesFoundException exception = assertThrows(CyclicDependenciesFoundException.class, () -> config.context());
                assertEquals(3, exception.components().size());
                assertThat(exception.components()).containsExactlyInAnyOrder(Component.class, Dependency.class, AnotherDependency.class);
            }

            public static Stream<Arguments> should_throw_exception_if_transitive_cyclic_dependencies_found() {
                List<Arguments> arguments = new ArrayList<>();
                for (Named component : List.of(
                    Named.of("Inject Constructor", CyclicComponentInjectConstructor.class),
                    Named.of("Inject Field", CyclicComponentInjectField.class),
                    Named.of("Inject Method", CyclicComponentInjectMethod.class))
                ) {
                    for (Named dependency : List.of(
                        Named.of("Inject Constructor", IndirectCyclicDependencyInjectConstructor.class),
                        Named.of("Inject Field", IndirectCyclicDependencyInjectField.class),
                        Named.of("Inject Method", IndirectCyclicDependencyInjectMethod.class)
                    )) {
                        for (Named anotherDependency : List.of(
                            Named.of("Inject Constructor", IndirectCyclicComponentInjectConstructor.class),
                            Named.of("Inject Field", IndirectCyclicComponentInjectField.class),
                            Named.of("Inject Method", IndirectCyclicComponentInjectMethod.class)
                        )) {
                            arguments.add(Arguments.of(component, dependency, anotherDependency));
                        }
                    }
                }
                return arguments.stream();

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
