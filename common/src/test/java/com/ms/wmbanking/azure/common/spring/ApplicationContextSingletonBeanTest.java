package com.ms.wmbanking.azure.common.spring;

import lombok.SneakyThrows;
import lombok.val;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class ApplicationContextSingletonBeanTest {

    @Mock
    private ApplicationContext applicationContext;

    @AfterEach
    void tearDown() {
        new ApplicationContextSingletonBean().clearApplicationContext();
    }

    @Test
    @SneakyThrows
    public void test_setApplicationContext() {

        assertNull(ApplicationContextSingletonBean.getApplicationContext());

        new ApplicationContextSingletonBean().setApplicationContext(applicationContext);
        assertSame(applicationContext, ApplicationContextSingletonBean.getApplicationContext());

        //  set twice - no error
        new ApplicationContextSingletonBean().setApplicationContext(applicationContext);

        //  clear
        new ApplicationContextSingletonBean().clearApplicationContext();
        assertNull(ApplicationContextSingletonBean.getApplicationContext());
    }

    @Test()
    @SneakyThrows
    public void test_setApplicationContext_anotherContext() {
        new ApplicationContextSingletonBean().setApplicationContext(applicationContext);
        assertSame(applicationContext, ApplicationContextSingletonBean.getApplicationContext());

        val anotherContext = mock(ApplicationContext.class);
        assertNotSame(applicationContext, anotherContext);

        assertThrows(IllegalArgumentException.class,
                     () -> new ApplicationContextSingletonBean().setApplicationContext(anotherContext));
    }
}