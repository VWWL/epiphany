package com.epiphany.context;

import com.epiphany.InjectionProvider;
import com.epiphany.context.exception.IllegalComponentException;
import com.epiphany.context.source.*;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.lang.reflect.ParameterizedType;
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
    private @Mock InjectionProvider<Dependency> dependencyProvider;

    @BeforeEach
    void setUp() throws NoSuchFieldException {
        when(context.get(Dependency.class)).thenReturn(Optional.of(dependency));
        when(context.get(Component.class)).thenReturn(Optional.of(componentInstance));
        ParameterizedType providerType = (ParameterizedType) InjectionTest.class.getDeclaredField("dependencyProvider").getGenericType();
        when(context.get(providerType)).thenReturn(Optional.of(dependencyProvider));
    }

    @Nested
    public class ConstructorInjection {

        @Nested
        class Injection {

            @Test
            void should_call_default_constructor_if_no_inject_constructor() {
                ComponentWithDefaultConstructor instance = new GeneralInjectionProvider<>(ComponentWithDefaultConstructor.class).get(context);
                assertNotNull(instance);
            }

            @Test
            void should_inject_dependency_via_injected_constructor() {
                ComponentWithInjectConstructor instance = new GeneralInjectionProvider<>(ComponentWithInjectConstructor.class).get(context);
                assertNotNull(instance);
            }

            @Test
            void should_include_dependency_from_inject_constructor() {
                GeneralInjectionProvider<ComponentWithInjectConstructor> provider = new GeneralInjectionProvider<>(ComponentWithInjectConstructor.class);
                assertThat(provider.dependencies()).containsExactly(Dependency.class);
            }

            @Test
            void should_inject_provider_via_inject_constructor() {
                ProviderInjectConstructor instance = (new GeneralInjectionProvider<>(ProviderInjectConstructor.class)).get(context);
                assertNotNull(instance.provider());
                assertSame(dependencyProvider, instance.provider());
            }

        }

        @Nested
        class IllegalInjectConstructor {

            @Test
            void should_throw_when_multi_inject_constructors_provided() {
                assertThrows(IllegalComponentException.class, () -> new GeneralInjectionProvider<>(ComponentWithMultiConstructorProvided.class));
            }

            @Test
            void should_throw_when_no_inject_or_default_constructor_provided() {
                assertThrows(IllegalComponentException.class, () -> new GeneralInjectionProvider<>(ComponentWithNoInjectAndDefaultConstructorProvided.class));
            }

            @Test
            void should_throw_if_component_is_abstract() {
                assertThrows(IllegalComponentException.class, () -> new GeneralInjectionProvider<>(AbstractComponent.class));
            }

            @Test
            void should_throw_if_component_is_interface() {
                assertThrows(IllegalComponentException.class, () -> new GeneralInjectionProvider<>(Component.class));
            }

        }

    }

    @Nested
    public class FieldInjection {

        @Nested
        class Injection {

            @Test
            void should_inject_dependency_via_field() {
                ComponentWithFieldInjection component = new GeneralInjectionProvider<>(ComponentWithFieldInjection.class).get(context);
                assertSame(dependency, component.dependency());
            }

            @Test
            void should_inject_subclass_dependency_via_field() {
                ComponentWithFieldInjection component = new GeneralInjectionProvider<>(SubclassWithComponentWithFieldInjection.class).get(context);
                assertSame(dependency, component.dependency());
            }

            @Test
            void should_include_field_dependency_in_denpendencies() {
                GeneralInjectionProvider<ComponentWithFieldInjection> provider = new GeneralInjectionProvider<>(ComponentWithFieldInjection.class);
                assertThat(provider.dependencies()).containsExactly(Dependency.class);
            }

        }

        @Nested
        class IllegalInjectField {

            @Test
            void should_throw_exception_when_inject_field_is_final() {
                assertThrows(IllegalComponentException.class, () -> new GeneralInjectionProvider<>(FinalInjectField.class));
            }

        }

    }

    @Nested
    public class MethodInjection {

        @Nested
        class Injection {

            @Test
            void should_call_inject_method_even_if_no_dependency_declared() {
                MethodInjectionWithNoDependency component = new GeneralInjectionProvider<>(MethodInjectionWithNoDependency.class).get(context);
                assertEquals(1, component.called());
            }

            @Test
            void should_inject_dependency_via_inject_method() {
                MethodInjectionWithDependency component = new GeneralInjectionProvider<>(MethodInjectionWithDependency.class).get(context);
                assertSame(dependency, component.dependency());
            }

            @Test
            void should_inclue_dependencies_from_inject_method() {
                GeneralInjectionProvider<MethodInjectionWithDependency> provider = new GeneralInjectionProvider<>(MethodInjectionWithDependency.class);
                assertThat(provider.dependencies()).containsExactly(Dependency.class);
            }

            @Test
            void should_inject_subclass_dependency_and_superclass_dependency() {
                SubClassWithInjectMethod component = new GeneralInjectionProvider<>(SubClassWithInjectMethod.class).get(context);
                assertSame(componentInstance, component.component());
                assertSame(dependency, component.dependency());
            }

            @Test
            void should_inclue_dependencies_from_inject_method_and_super_class_inject_method_and_called_super_first() {
                GeneralInjectionProvider<SubClassWithInjectMethod> provider = new GeneralInjectionProvider<>(SubClassWithInjectMethod.class);
                assertThat(provider.dependencies()).containsExactly(Dependency.class, Component.class);
            }

            @Test
            void should_only_call_once_if_subclass_override_inject_method_with_inject() {
                SubClassOverrideSuperClassWithInject component = new GeneralInjectionProvider<>(SubClassOverrideSuperClassWithInject.class).get(context);
                assertNull(component.dependency());
            }

            @Test
            void should_not_call_inject_method_if_override_with_no_inject() {
                SubClassOverrideSuperClassWithNoInject component = new GeneralInjectionProvider<>(SubClassOverrideSuperClassWithNoInject.class).get(context);
                assertNull(component.dependency());
            }

        }

        @Nested
        class IllegalInjectionMethods {

            @Test
            void should_throw_exception_if_inject_method_has_type_parameter() {
                assertThrows(IllegalComponentException.class, () -> new GeneralInjectionProvider<>(InjectMethodWithTypeParameter.class));
            }

        }

        @Nested
        class ParseMethodIsOverride {

            private SubclassOfCompareInjectMethodOverride component;

            @BeforeEach
            void setUp() {
                component = new GeneralInjectionProvider<>(SubclassOfCompareInjectMethodOverride.class).get(context);
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

    @Nested
    public class ExplicitInjection {

        private @Mock InjectionsWithDependency injectionsWithDependency;

        @BeforeEach
        void setUp() {
            when(injectionsWithDependency.dependency(componentInstance)).thenReturn(dependency);
            when(context.get(InjectionsWithDependency.class)).thenReturn(Optional.of(injectionsWithDependency));
        }

        @Test
        void should_register_component_using_injections() throws NoSuchMethodException {
            Dependency component = new ExplicitInjectionProvider<InjectionsWithDependency, Dependency>(InjectionsWithDependency.class, InjectionsWithDependency.class.getDeclaredMethod("dependency", Component.class)).get(context);
            assertSame(dependency, component);
        }

    }

}
