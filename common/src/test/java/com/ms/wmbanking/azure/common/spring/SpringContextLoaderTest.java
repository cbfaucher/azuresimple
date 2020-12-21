package com.ms.wmbanking.azure.common.spring;

import lombok.SneakyThrows;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles(profiles = {"dev", "test"})
class SpringContextLoaderTest {

    @BeforeEach
    void tearDown() {
        SpringContextLoader.clearGlobalSpringMainClass();
    }

    @Test
    @SneakyThrows
    public void testFlow() {
        SpringContextLoader.setGlobalSpringMainClassName(MyTestApplication.class.getName());

        //  set same - no error
        SpringContextLoader.setGlobalSpringMainClassName(MyTestApplication.class.getName());

        //  set different - error
        assertThrows(IllegalArgumentException.class, () -> SpringContextLoader.setGlobalSpringMainClassName(getClass().getName()));

        assertNull(ApplicationContextSingletonBean.getApplicationContext());

        //  load
        val context = SpringContextLoader.getApplicationContext();

        assertSame(context, ApplicationContextSingletonBean.getApplicationContext());

        //  destroy context
        ((ConfigurableApplicationContext) context).close();

        assertNull(ApplicationContextSingletonBean.getApplicationContext());
    }

    @Test
    @SneakyThrows
    public void testSetApplicationClassFromSystemPooperty() {
        assertNull(SpringContextLoader.getGlobalSpringMainClassName());
        assertNull(ApplicationContextSingletonBean.getApplicationContext());

        val originalMainClass = System.setProperty(SpringContextLoader.SYSPROP_MAIN_CLASS,
                                                   MyTestApplication.class.getName());

        try {
            SpringContextLoader.setSpringMainClassFromSystemPropertyIfNeeded();
            assertSame(MyTestApplication.class.getName(), SpringContextLoader.getGlobalSpringMainClassName());
        } finally {
            if (originalMainClass != null) {
                System.setProperty(SpringContextLoader.SYSPROP_MAIN_CLASS, originalMainClass);
            } else {
                System.clearProperty(SpringContextLoader.SYSPROP_MAIN_CLASS);
            }
        }
    }
}