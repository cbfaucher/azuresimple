package com.ms.wmbanking.azure.common.spring;

import com.ms.wmbanking.azure.common.testutils.SimpleExecutionContext;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Vector;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = AzureFunctionSpringHookTest.HookApplication.class)
@ActiveProfiles("test")
@DirtiesContext
@ExtendWith(SpringExtension.class)
class AzureFunctionSpringHookTest extends AzureFunctionSpringHook<String, Integer> {

    @Autowired
    private Function<String, Integer> function;

    @Autowired
    private Supplier<Integer> supplier;

    @Autowired
    private Consumer<String> consumer;

    @BeforeEach
    void setUp() {
        Mockito.reset(function, supplier, consumer);    //created as Spring beans, so they live for all class lifespan
    }

    @Test
    @SneakyThrows
    public void test_handleRequest_Function() {
        val context = new SimpleExecutionContext("function");
        when(function.apply(anyString())).thenReturn(987654);

        val actual = handleRequest("blerg", context);
        assertEquals(987654, actual);

        verifyZeroInteractions(supplier, consumer);
    }

    @Test
    @SneakyThrows
    public void test_handleRequest_Supplier() {
        val context = new SimpleExecutionContext("supplier");
        when(supplier.get()).thenReturn(112233);

        val actual = handleRequest(context);
        assertEquals(112233, actual);

        verifyZeroInteractions(function, consumer);
    }

    @Test
    @SneakyThrows
    public void test_handleRequest_Consumer() {
        val context = new SimpleExecutionContext("consumer");

        val actual = handleRequest("booyah", context);
        assertNull(actual);
        verify(consumer).accept("booyah");

        verifyZeroInteractions(function, supplier);
    }

    @Test
    @SneakyThrows
    public void test_handleRequest_invalidType() {
        val context = new SimpleExecutionContext("functionWithBadType");

        assertThrows(SpringFailureException.class, () -> handleRequest("data", context));
    }

    @Test
    @SneakyThrows
    public void test_handleRequest_beanIsUnsupportedType() {
        val context = new SimpleExecutionContext("somethingElse");

        assertThrows(SpringFailureException.class, () -> handleRequest("data", context));
    }

    @Import(AzureFunctionsSpringBeans.class)
    static public class HookApplication {

        @Bean
        public Function<String, Integer> function() {
            return mock(Function.class);
        }

        @Bean
        public Supplier<Integer> supplier() {
            return mock(Supplier.class);
        }

        @Bean
        public Consumer<String> consumer() {
            return mock(Consumer.class);
        }

        @Bean
        public Function<List<?>, String> functionWithBadType() {
            return Object::toString;
        }

        @Bean
        public Vector somethingElse() {
            return new Vector();
        }
    }
}