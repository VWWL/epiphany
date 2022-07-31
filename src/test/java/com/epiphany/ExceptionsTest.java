package com.epiphany;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
public class ExceptionsTest {
    private @Mock TestForExceptions exceptions;

    @Test
    void should_wrap_exception_for_void_functions() throws IOException {
        RuntimeException runtimeException = new RuntimeException();
        doThrow(runtimeException).when(exceptions).execute();
        assertThatThrownBy(() -> Exceptions.execute(exceptions::execute).run()).isInstanceOf(RuntimeException.class).isSameAs(runtimeException);
    }

    @Test
    void should_wrap_unchecked_exception_for_void_functions() throws IOException {
        doThrow(new IOException()).when(exceptions).execute();
        assertThatThrownBy(() -> Exceptions.execute(exceptions::execute).run()).isInstanceOf(RuntimeException.class);
    }

    @Test
    void should_wrap_exception_for_functions_with_result() throws IOException {
        RuntimeException runtimeException = new RuntimeException();
        when(exceptions.evaluate()).thenThrow(runtimeException);
        assertThatThrownBy(() -> Exceptions.evaluate(exceptions::evaluate).evaluate()).isInstanceOf(RuntimeException.class).isSameAs(runtimeException);
    }

    @Test
    void should_wrap_unchecked_exception_for_functions_with_result() throws IOException {
        when(exceptions.evaluate()).thenThrow(new IOException());
        assertThatThrownBy(() -> Exceptions.evaluate(exceptions::evaluate).evaluate()).isInstanceOf(RuntimeException.class);
    }

    @Test
    void should_be_able_to_execute_method_when_method_is_good() throws IOException {
        when(exceptions.evaluate()).thenReturn("");
        Exceptions.execute(exceptions::execute).run();
        then(exceptions).should(only()).execute();
    }

    @Test
    void should_execute_exception_for_wanted_message() throws IOException {
        doThrow(new IOException()).when(exceptions).execute();
        assertThatThrownBy(() -> Exceptions.execute(exceptions::execute).elseThrow("message"))
                .isInstanceOf(RuntimeException.class).hasMessage("message");
    }

    @Test
    void should_execute_exception_when_method_is_good() {
        assertDoesNotThrow(() -> Exceptions.execute(exceptions::execute).elseThrow("message"));
    }

    @Test
    void should_execute_method_when_method_is_good() throws IOException {
        when(exceptions.evaluate()).thenReturn("");
        Exceptions.ignored(exceptions::execute);
        then(exceptions).should(only()).execute();
    }

    @Test
    void should_not_throw_when_method_throws() throws IOException {
        doThrow(new RuntimeException()).when(exceptions).execute();
        assertDoesNotThrow(() -> Exceptions.ignored(exceptions::execute));
    }

    @Test
    void should_evaluate_exception_for_wanted_message() throws IOException {
        doThrow(new IOException()).when(exceptions).evaluate();
        assertThatThrownBy(() -> Exceptions.evaluate(exceptions::evaluate).elseThrow("message"))
                .isInstanceOf(RuntimeException.class).hasMessage("message");
    }

    @Test
    void should_evaluate_exception_when_method_is_good() {
        assertDoesNotThrow(() -> Exceptions.evaluate(exceptions::evaluate).elseThrow("message"));
    }


    @Test
    void should_evaluate_ignored_method_when_method_is_good() throws IOException {
        when(exceptions.evaluate()).thenReturn("");
        assertEquals("", Exceptions.ignored(exceptions::evaluate, ""));
    }

    @Test
    void should_return_default_when_evaluate_method_throws() throws IOException {
        when(exceptions.evaluate()).thenThrow(new IOException());
        assertEquals("abc", Exceptions.ignored(exceptions::evaluate, "abc"));
    }

    @Test
    void should_be_able_to_evaluate_method_when_method_is_good() {
        assertEquals(Exceptions.evaluate(() -> "test").evaluate(), "test");
    }

    @SuppressWarnings("all")
    private interface TestForExceptions {
        void execute() throws IOException;
        String evaluate() throws IOException;
    }
}
