package com.epiphany.context;

import jakarta.inject.Inject;
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

        @Nested
        public class ConstructorInjection {

            @Test
            void should_bind_class_to_a_default_constructor() {
                config.bind(Component.class, ComponentWithDefaultConstructor.class);
                Component instance = config.context().get(Component.class).get();
                assertNotNull(instance);
                assertTrue(instance instanceof ComponentWithDefaultConstructor);
            }

            @Test
            void should_bind_type_to_a_inject_constructor() {
                Dependency dependency = new Dependency() {};
                config.bind(Component.class, ComponentWithInjectConstructor.class);
                config.bind(Dependency.class, dependency);
                Component instance = config.context().get(Component.class).get();
                assertNotNull(instance);
                assertSame(dependency, ((ComponentWithInjectConstructor) instance).dependency());
            }

            @Test
            void should_bind_type_to_a_class_with_transitive_dependencies() {
                config.bind(Component.class, ComponentWithInjectConstructor.class);
                config.bind(Dependency.class, DependencyWithInjectConstructor.class);
                config.bind(String.class, "Indirect dependency");
                Component instance = config.context().get(Component.class).get();
                assertNotNull(instance);
                Dependency dependency = ((ComponentWithInjectConstructor) instance).dependency();
                assertNotNull(dependency);
                assertSame("Indirect dependency", ((DependencyWithInjectConstructor) dependency).dependency());
            }

            @Test
            void should_throw_when_multi_inject_constructors_provided() {
                assertThrows(IllegalComponentException.class, () -> config.bind(Component.class, ComponentWithMultiConstructorProvided.class));
            }

            @Test
            void should_throw_when_no_inject_or_default_constructor_provided() {
                assertThrows(IllegalComponentException.class, () -> config.bind(Component.class, ComponentWithNoInjectAndDefaultConstructorProvided.class));
            }

            @Test
            void should_throw_when_dependency_is_not_found() {
                config.bind(Component.class, ComponentWithInjectConstructor.class);
                DependencyNotFoundException exception = assertThrows(DependencyNotFoundException.class, () -> config.context());
                assertEquals(Dependency.class, exception.dependency());
                assertEquals(Component.class, exception.component());
            }

            @Test
            void should_throw_if_transitive_dependency_not_found() {
                config.bind(Component.class, ComponentWithInjectConstructor.class);
                config.bind(Dependency.class, DependencyWithInjectConstructor.class);
                DependencyNotFoundException exception = assertThrows(DependencyNotFoundException.class, () -> config.context());
                assertEquals(String.class, exception.dependency());
                assertEquals(Dependency.class, exception.component());
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
            void should_throw_if_component_is_abstract() {
                assertThrows(IllegalComponentException.class, () -> new ConstructorInjectionProvider<>(AbstractComponent.class));
            }

            @Test
            void should_throw_if_component_is_interface() {
                assertThrows(IllegalComponentException.class, () -> new ConstructorInjectionProvider<>(Component.class));
            }

        }

        @Nested
        public class FieldInjection {

            @Test
            void should_inject_dependency_via_field() {
                Dependency dependency = new Dependency() {};
                config.bind(Dependency.class, dependency);
                config.bind(ComponentWithFieldInjection.class, ComponentWithFieldInjection.class);
                ComponentWithFieldInjection component = config.context().get(ComponentWithFieldInjection.class).get();
                assertSame(dependency, component.dependency());
            }

            @Test
            void should_inject_subclass_dependency_via_field() {
                Dependency dependency = new Dependency() {};
                config.bind(Dependency.class, dependency);
                config.bind(ComponentWithFieldInjection.class, SubclassWithComponentWithFieldInjection.class);
                ComponentWithFieldInjection component = config.context().get(ComponentWithFieldInjection.class).get();
                assertSame(dependency, component.dependency());
            }

            @Test
            void should_include_field_dependency_in_denpendencies() {
                ConstructorInjectionProvider<ComponentWithFieldInjection> provider = new ConstructorInjectionProvider<>(ComponentWithFieldInjection.class);
                assertThat(provider.dependencies()).containsExactly(Dependency.class);
            }

            @Test
            void should_throw_exception_when_field_with_cyclic_dependencies() {
                config.bind(ComponentWithComponentInjection.class, ComponentWithComponentInjection.class);
                assertThrows(CyclicDependenciesFoundException.class, () -> config.context());
            }

        }

        @Nested
        public class MethodInjection {

            @Test
            void should_call_inject_method_even_if_no_dependency_declared() {
                config.bind(MethodInjectionWithNoDependency.class, MethodInjectionWithNoDependency.class);
                Optional<MethodInjectionWithNoDependency> injection = config.context().get(MethodInjectionWithNoDependency.class);
                assertEquals(1, injection.get().called());
            }

            @Test
            void should_inject_dependency_via_inject_method() {
                Dependency dependency = new Dependency() {};
                config.bind(Dependency.class, dependency);
                config.bind(MethodInjectionWithDependency.class, MethodInjectionWithDependency.class);
                Optional<MethodInjectionWithDependency> injection = config.context().get(MethodInjectionWithDependency.class);
                assertSame(dependency, injection.get().dependency());
            }

            @Test
            void should_inclue_dependencies_from_inject_method() {
                ConstructorInjectionProvider<MethodInjectionWithDependency> provider = new ConstructorInjectionProvider<>(MethodInjectionWithDependency.class);
                assertThat(provider.dependencies()).containsExactly(Dependency.class);
            }

            @Test
            void should_inject_subclass_dependency_and_superclass_dependency() {
                Dependency dependency = new Dependency() {};
                Component componentInstance = new Component() {};
                config.bind(Dependency.class, dependency);
                config.bind(Component.class, componentInstance);
                config.bind(SubClassWithInjectMethod.class, SubClassWithInjectMethod.class);
                SubClassWithInjectMethod component = config.context().get(SubClassWithInjectMethod.class).get();
                assertSame(componentInstance, component.component());
                assertSame(dependency, component.dependency());
            }

            @Test
            void inclue_dependencies_from_inject_method_and_super_class_inject_method_and_called_super_first() {
                ConstructorInjectionProvider<SubClassWithInjectMethod> provider = new ConstructorInjectionProvider<>(SubClassWithInjectMethod.class);
                assertThat(provider.dependencies()).containsExactly(Dependency.class, Component.class);
            }

            @Test
            void should_only_call_once_if_subclass_override_inject_method_with_inject() {
                Dependency dependency = new Dependency() {};
                config.bind(Dependency.class, dependency);
                config.bind(SubClassOverrideSuperClassWithInject.class, SubClassOverrideSuperClassWithInject.class);
                SubClassOverrideSuperClassWithInject component = config.context().get(SubClassOverrideSuperClassWithInject.class).get();
                assertNull(component.dependency());
            }

            @Test
            void should_not_call_inject_method_if_override_with_no_inject() {
                config.bind(SubClassOverrideSuperClassWithNoInject.class, SubClassOverrideSuperClassWithNoInject.class);
                SubClassOverrideSuperClassWithNoInject component = config.context().get(SubClassOverrideSuperClassWithNoInject.class).get();
                assertNull(component.dependency());
            }

            @Nested
            class ParseMethodIsOverride {

                private SubclassOfCompareInjectMethodOverride component;

                @BeforeEach
                void setUp() {
                    config.bind(String.class, "");
                    config.bind(Dependency.class, new Dependency() {});
                    config.bind(SubclassOfCompareInjectMethodOverride.class, SubclassOfCompareInjectMethodOverride.class);
                    component = config.context().get(SubclassOfCompareInjectMethodOverride.class).get();
                }

                @Test
                void should_findout_which_is_override_method_when_method_is_same() {
                    assertEquals(0, component.methodWithSameNameAndParameters());
                }

                @Test
                void should_findout_which_is_override_method_when_method_name_is_different() {
                    assertEquals(1, component.methodWithNameNotSame());
                }

                @Test
                void should_findout_which_is_override_method_when_method_parameters_is_different() {
                    assertEquals(1, component.methodWithParametersNotSame());
                }

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

interface Component {}

interface Dependency {}

interface NestedDependency {}

abstract class AbstractComponent implements Component {
}

@SuppressWarnings("unused")
class CompareInjectMethodOverride {

    private int methodWithSameNameAndParameters;
    private int methodWithParametersNotSame;
    private int methodWithNameNotSame;

    @Inject
    public void methodWithSameNameAndParameters(String p1) {
        methodWithSameNameAndParameters++;
    }

    @Inject
    public void methodWithParametersNotSame(String p1, Dependency p2, String p3) {
        methodWithParametersNotSame++;
    }

    @Inject
    public void methodWithNameNotSame(String p1) {
        methodWithNameNotSame++;
    }

    public int methodWithSameNameAndParameters() {
        return methodWithSameNameAndParameters;
    }

    public int methodWithParametersNotSame() {
        return methodWithParametersNotSame;
    }

    public int methodWithNameNotSame() {
        return methodWithNameNotSame;
    }

}

@SuppressWarnings("unused")
class SubclassOfCompareInjectMethodOverride extends CompareInjectMethodOverride {

    @Inject
    @Override
    public void methodWithSameNameAndParameters(String p1) {
    }

    @Inject
    public void methodWithParametersNotSame(Dependency p1, String p2, String p3) {
    }

    public void methodWithParametersNotSame(String p1, String p2, Dependency p3) {
    }

    @Inject
    public void theNotSameMethodOfMethodWithNameNotSame(String p1) {
    }

    public void theNotSameMethodOfMethodWithNameNotSame2(String p1) {
    }

}

class SubClassOverrideSuperClassWithInject extends SuperClassWithInjectMethod {

    @Inject
    @Override
    public void inject(Dependency dependency) {
    }

}

class SubClassOverrideSuperClassWithNoInject extends SuperClassWithInjectMethod {

    @Override
    public void inject(Dependency dependency) {
        super.inject(dependency);
    }

}

@SuppressWarnings("unused")
class SuperClassWithInjectMethod {

    private Dependency dependency;

    @Inject
    public void inject(Dependency dependency) {
        this.dependency = dependency;
    }

    public Dependency dependency() {
        return dependency;
    }

}

@SuppressWarnings("unused")
class SubClassWithInjectMethod extends SuperClassWithInjectMethod {

    private Component component;

    @Inject
    private void injectComponent(Component component) {
        this.component = component;
    }

    public Component component() {
        return component;
    }

}

@SuppressWarnings("unused")
class MethodInjectionWithNoDependency {

    private int called = 0;

    @Inject
    private void install() {
        called++;
    }

    public int called() {
        return called;
    }

}

@SuppressWarnings("unused")
class MethodInjectionWithDependency {

    private Dependency dependency;

    @Inject
    private void injectDependency(Dependency dependency) {
        this.dependency = dependency;
    }

    public Dependency dependency() {
        return dependency;
    }

}

class ComponentWithFieldInjection {

    private @Inject Dependency dependency;

    public Dependency dependency() {
        return dependency;
    }

}

@SuppressWarnings("unused")
class ComponentWithComponentInjection {

    private @Inject ComponentWithComponentInjection component;

}

class SubclassWithComponentWithFieldInjection extends ComponentWithFieldInjection {
}

class ComponentWithDefaultConstructor implements Component {

    public ComponentWithDefaultConstructor() {
    }

}

class ComponentWithInjectConstructor implements Component {

    private final Dependency dependency;

    public @Inject ComponentWithInjectConstructor(final Dependency dependency) {
        this.dependency = dependency;
    }

    public Dependency dependency() {
        return dependency;
    }

}

@SuppressWarnings("unused")
class ComponentWithMultiConstructorProvided implements Component {

    public @Inject ComponentWithMultiConstructorProvided(final String name, final Double value) {
    }

    public @Inject ComponentWithMultiConstructorProvided(final String name) {
    }

}

@SuppressWarnings("unused")
class ComponentWithNoInjectAndDefaultConstructorProvided implements Component {

    public ComponentWithNoInjectAndDefaultConstructorProvided(final String name, final Double value) {
    }

}

class DependencyWithInjectConstructor implements Dependency {

    private final String dependency;

    public @Inject DependencyWithInjectConstructor(final String dependency) {
        this.dependency = dependency;
    }

    public String dependency() {
        return dependency;
    }

}

@SuppressWarnings("unused")
class DependencyDependedOnComponent implements Dependency {

    public @Inject DependencyDependedOnComponent(final Component component) {
    }

}

@SuppressWarnings("unused")
class DependencyWithNestedDependency implements Dependency {

    public @Inject DependencyWithNestedDependency(NestedDependency dependency) {
    }

}

@SuppressWarnings("unused")
class NestedDependencyOnComponent implements NestedDependency {

    public @Inject NestedDependencyOnComponent(final Component component) {
    }

}
