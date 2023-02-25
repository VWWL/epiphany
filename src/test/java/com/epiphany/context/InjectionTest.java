package com.epiphany.context;

import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SuppressWarnings("all")
@MockitoSettings(strictness = Strictness.LENIENT)
public class InjectionTest {

    private @Mock Dependency dependency;
    private @Mock Component componentInstance;
    private @Mock Context context;

    @BeforeEach
    void setUp() {
        when(context.get(Dependency.class)).thenReturn(Optional.of(dependency));
        when(context.get(Component.class)).thenReturn(Optional.of(componentInstance));
    }

    @Nested
    public class ConstructorInjection {

        @Nested
        class Injection {

            @Test
            void should_call_default_constructor_if_no_inject_constructor() {
                ComponentWithDefaultConstructor instance = new ConstructorInjectionProvider<>(ComponentWithDefaultConstructor.class).get(context);
                assertNotNull(instance);
            }

            @Test
            void should_inject_dependency_via_injected_constructor() {
                ComponentWithInjectConstructor instance = new ConstructorInjectionProvider<>(ComponentWithInjectConstructor.class).get(context);
                assertNotNull(instance);
            }

            @Test
            void should_include_dependency_from_inject_constructor() {
                ConstructorInjectionProvider<ComponentWithInjectConstructor> provider = new ConstructorInjectionProvider<>(ComponentWithInjectConstructor.class);
                assertThat(provider.dependencies()).containsExactly(Dependency.class);
            }

        }

        @Nested
        class IllegalInjectConstructor {

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

        }

    }

    @Nested
    public class FieldInjection {

        @Nested
        class Injection {

            @Test
            void should_inject_dependency_via_field() {
                ComponentWithFieldInjection component = new ConstructorInjectionProvider<>(ComponentWithFieldInjection.class).get(context);
                assertSame(dependency, component.dependency());
            }

            @Test
            void should_inject_subclass_dependency_via_field() {
                ComponentWithFieldInjection component = new ConstructorInjectionProvider<>(SubclassWithComponentWithFieldInjection.class).get(context);
                assertSame(dependency, component.dependency());
            }

            @Test
            void should_include_field_dependency_in_denpendencies() {
                ConstructorInjectionProvider<ComponentWithFieldInjection> provider = new ConstructorInjectionProvider<>(ComponentWithFieldInjection.class);
                assertThat(provider.dependencies()).containsExactly(Dependency.class);
            }

        }

        @Nested
        class IllegalInjectField {

            @Test
            void should_throw_exception_when_inject_field_is_final() {
                assertThrows(IllegalComponentException.class, () -> new ConstructorInjectionProvider<>(FinalInjectField.class));
            }

        }

    }

    @Nested
    public class MethodInjection {

        @Nested
        class Injection {

            @Test
            void should_call_inject_method_even_if_no_dependency_declared() {
                MethodInjectionWithNoDependency component = new ConstructorInjectionProvider<>(MethodInjectionWithNoDependency.class).get(context);
                assertEquals(1, component.called());
            }

            @Test
            void should_inject_dependency_via_inject_method() {
                MethodInjectionWithDependency component = new ConstructorInjectionProvider<>(MethodInjectionWithDependency.class).get(context);
                assertSame(dependency, component.dependency());
            }

            @Test
            void should_inclue_dependencies_from_inject_method() {
                ConstructorInjectionProvider<MethodInjectionWithDependency> provider = new ConstructorInjectionProvider<>(MethodInjectionWithDependency.class);
                assertThat(provider.dependencies()).containsExactly(Dependency.class);
            }

            @Test
            void should_inject_subclass_dependency_and_superclass_dependency() {
                SubClassWithInjectMethod component = new ConstructorInjectionProvider<>(SubClassWithInjectMethod.class).get(context);
                assertSame(componentInstance, component.component());
                assertSame(dependency, component.dependency());
            }

            @Test
            void should_inclue_dependencies_from_inject_method_and_super_class_inject_method_and_called_super_first() {
                ConstructorInjectionProvider<SubClassWithInjectMethod> provider = new ConstructorInjectionProvider<>(SubClassWithInjectMethod.class);
                assertThat(provider.dependencies()).containsExactly(Dependency.class, Component.class);
            }

            @Test
            void should_only_call_once_if_subclass_override_inject_method_with_inject() {
                SubClassOverrideSuperClassWithInject component = new ConstructorInjectionProvider<>(SubClassOverrideSuperClassWithInject.class).get(context);
                assertNull(component.dependency());
            }

            @Test
            void should_not_call_inject_method_if_override_with_no_inject() {
                SubClassOverrideSuperClassWithNoInject component = new ConstructorInjectionProvider<>(SubClassOverrideSuperClassWithNoInject.class).get(context);
                assertNull(component.dependency());
            }

        }

        @Nested
        class IllegalInjectionMethods {

            @Test
            void should_throw_exception_if_inject_method_has_type_parameter() {
                assertThrows(IllegalComponentException.class, () -> new ConstructorInjectionProvider<>(InjectMethodWithTypeParameter.class));
            }

        }

        @Nested
        class ParseMethodIsOverride {

            private SubclassOfCompareInjectMethodOverride component;

            @BeforeEach
            void setUp() {
                component = new ConstructorInjectionProvider<>(SubclassOfCompareInjectMethodOverride.class).get(context);
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
