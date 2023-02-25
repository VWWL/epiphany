package com.epiphany.context;

import org.junit.jupiter.api.*;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("all")
public class InjectionTest {

    private ContextConfig config;
    private Dependency dependency;

    @BeforeEach
    void setUp() {
        this.config = new ContextConfig();
        this.dependency = new Dependency() {};
        config.bind(Dependency.class, dependency);
    }

    @Nested
    public class ConstructorInjection {

        @Test
        void should_bind_class_to_a_default_constructor() {
            Component instance = getComponent(Component.class, ComponentWithDefaultConstructor.class);
            assertNotNull(instance);
            assertTrue(instance instanceof ComponentWithDefaultConstructor);
        }

        @Test
        void should_bind_type_to_a_inject_constructor() {
            Component instance = getComponent(Component.class, ComponentWithInjectConstructor.class);
            assertNotNull(instance);
            assertSame(dependency, ((ComponentWithInjectConstructor) instance).dependency());
        }

        @Test
        void should_bind_type_to_a_class_with_transitive_dependencies() {
            config.bind(Dependency.class, DependencyWithInjectConstructor.class);
            config.bind(String.class, "Indirect dependency");
            Component instance = getComponent(Component.class, ComponentWithInjectConstructor.class);
            assertNotNull(instance);
            Dependency dependency = ((ComponentWithInjectConstructor) instance).dependency();
            assertNotNull(dependency);
            assertSame("Indirect dependency", ((DependencyWithInjectConstructor) dependency).dependency());
        }

        @Test
        void should_throw_when_multi_inject_constructors_provided() {
            assertThrows(IllegalComponentException.class, () -> new ConstructorInjectionProvider<>(ComponentWithMultiConstructorProvided.class));
        }

        @Test
        void should_throw_when_no_inject_or_default_constructor_provided() {
            assertThrows(IllegalComponentException.class, () -> new ConstructorInjectionProvider<>(ComponentWithNoInjectAndDefaultConstructorProvided.class));
        }

        @Test
        void should_throw_if_component_is_abstract() {
            assertThrows(IllegalComponentException.class, () -> new ConstructorInjectionProvider<>(AbstractComponent.class));
        }

        @Test
        void should_throw_if_component_is_interface() {
            assertThrows(IllegalComponentException.class, () -> new ConstructorInjectionProvider<>(Component.class));
        }

        @Test
        void should_include_dependency_from_inject_constructor() {
            ConstructorInjectionProvider<ComponentWithInjectConstructor> provider = new ConstructorInjectionProvider<>(ComponentWithInjectConstructor.class);
            assertThat(provider.dependencies()).containsExactly(Dependency.class);
        }

    }

    @Nested
    public class FieldInjection {

        @Test
        void should_inject_dependency_via_field() {
            ComponentWithFieldInjection component = getComponent(ComponentWithFieldInjection.class, ComponentWithFieldInjection.class);
            assertSame(dependency, component.dependency());
        }

        @Test
        void should_inject_subclass_dependency_via_field() {
            ComponentWithFieldInjection component = getComponent(ComponentWithFieldInjection.class, SubclassWithComponentWithFieldInjection.class);
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

        @Test
        void should_throw_exception_when_inject_field_is_final() {
            assertThrows(IllegalComponentException.class, () -> new ConstructorInjectionProvider<>(FinalInjectField.class));
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
            MethodInjectionWithDependency component = getComponent(MethodInjectionWithDependency.class, MethodInjectionWithDependency.class);
            assertSame(dependency, component.dependency());
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
            SubClassWithInjectMethod component = getComponent(SubClassWithInjectMethod.class, SubClassWithInjectMethod.class);
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
            SubClassOverrideSuperClassWithInject component = getComponent(SubClassOverrideSuperClassWithInject.class, SubClassOverrideSuperClassWithInject.class);
            assertNull(component.dependency());
        }

        @Test
        void should_not_call_inject_method_if_override_with_no_inject() {
            SubClassOverrideSuperClassWithNoInject component = getComponent(SubClassOverrideSuperClassWithNoInject.class, SubClassOverrideSuperClassWithNoInject.class);
            assertNull(component.dependency());
        }

        @Nested
        class ParseMethodIsOverride {

            private SubclassOfCompareInjectMethodOverride component;

            @BeforeEach
            void setUp() {
                config.bind(String.class, "");
                config.bind(Dependency.class, new Dependency() {});
                component = getComponent(SubclassOfCompareInjectMethodOverride.class, SubclassOfCompareInjectMethodOverride.class);
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

        @Test
        void should_throw_exception_if_inject_method_has_type_parameter() {
            assertThrows(IllegalComponentException.class, () -> new ConstructorInjectionProvider<>(InjectMethodWithTypeParameter.class));
        }

    }

    private <T, R extends T> T getComponent(Class<T> type, Class<R> implementation) {
        config.bind(type, implementation);
        return config.context().get(type).get();
    }

}
