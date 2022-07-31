package com.epiphany;

import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ContainerTest {
    private Context context;

    @BeforeEach
    void setUp() {
        context = new Context();
    }

    @Nested
    public class ComponentConstruction {
        @Test
        void should_bind_type_to_a_specific_instance() {
            Component instance = new Component() {
            };
            context.bind(Component.class, instance);
            assertSame(instance, context.get(Component.class));
        }

        @Nested
        public class ConstructorInjection {
            @Test
            void should_bind_class_to_a_default_constructor() {
                context.bind(Component.class, ComponentWithDefaultConstructor.class);
                Component instance = context.get(Component.class);
                assertNotNull(instance);
                assertTrue(instance instanceof ComponentWithDefaultConstructor);
            }

            @Test
            void should_bind_type_to_a_inject_constructor() {
                Dependency dependency = new Dependency() {
                };
                context.bind(Component.class, ComponentWithInjectConstructor.class);
                context.bind(Dependency.class, dependency);
                Component instance = context.get(Component.class);
                assertNotNull(instance);
                assertSame(dependency, ((ComponentWithInjectConstructor) instance).dependency());
            }

            @Test
            void should_bind_type_to_a_class_with_transitive_dependencies() {
                context.bind(Component.class, ComponentWithInjectConstructor.class);
                context.bind(Dependency.class, DependencyWithInjectConstructor.class);
                context.bind(String.class, "Indirect dependency");
                Component instance = context.get(Component.class);
                assertNotNull(instance);
                Dependency dependency = ((ComponentWithInjectConstructor) instance).dependency();
                assertNotNull(dependency);
                assertSame("Indirect dependency", ((DependencyWithInjectConstructor) dependency).dependency());
            }

            @Test
            void should_throw_when_multi_inject_constructors_provided() {
                assertThrows(
                        IllegalComponentException.class,
                        () -> context.bind(Component.class, ComponentWithMultiConstructorProvided.class)
                );
            }

            @Test
            void should_throw_when_no_inject_or_default_constructor_provided() {
                assertThrows(
                        IllegalComponentException.class,
                        () -> context.bind(Component.class, ComponentWithNoInjectAndDefaultConstructorProvided.class)
                );
            }

            @Test
            void should_throw_when_dependency_is_not_found() {
                assertThrows(
                        ComponentNotFoundException.class,
                        () -> context.get(Component.class)
                );
            }
        }

        @Nested
        public class FieldInjection {

        }

        @Nested
        public class MethodInjection {

        }
    }

    @Nested
    public class DependenciesSelection {

    }

    @Nested
    public class LifecycleManagement {

    }
}

interface Component {
}

interface Dependency {
}

class ComponentWithDefaultConstructor implements Component {
    public ComponentWithDefaultConstructor() {
    }
}

class ComponentWithInjectConstructor implements Component {
    private final Dependency dependency;

    @Inject
    public ComponentWithInjectConstructor(final Dependency dependency) {
        this.dependency = dependency;
    }

    public Dependency dependency() {
        return dependency;
    }

}

@SuppressWarnings("unused")
class ComponentWithMultiConstructorProvided implements Component {
    @Inject
    public ComponentWithMultiConstructorProvided(final String name, final Double value) {
    }

    @Inject
    public ComponentWithMultiConstructorProvided(final String name) {
    }
}

@SuppressWarnings("unused")
class ComponentWithNoInjectAndDefaultConstructorProvided implements Component {
    public ComponentWithNoInjectAndDefaultConstructorProvided(final String name, final Double value) {
    }
}

class DependencyWithInjectConstructor implements Dependency {

    private final String dependency;

    @Inject
    public DependencyWithInjectConstructor(final String dependency) {
        this.dependency = dependency;
    }

    public String dependency() {
        return dependency;
    }
}
