package com.ms.wmbanking.azure.common.spring;

import lombok.Getter;
import lombok.NonNull;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Optional;

/**
 * Dynamically load a {@link ConfigurableApplicationContext} out of {@link #globalSpringMainClassName} if no
 * application already loaded in {@link ApplicationContextSingletonBean}.
 *
 * WARNING!!!  This is ClassLaoder-sensitive.  Azure loads each Function in its own ClassLoader.  So you do end up
 * with <b>multiple</b> {@link ConfigurableApplicationContext} being loaded (and such, pools, db connections, etc.)
 * due to this.
 */
class SpringContextLoader {

    static private final Object lock = ApplicationContextSingletonBean.lock;

    static private final Logger log = LogManager.getLogger(SpringContextLoader.class);
    public static final String SYSPROP_MAIN_CLASS = "MAIN_CLASS";

    @Getter
    static private String globalSpringMainClassName = null;

    static void setGlobalSpringMainClassName(@NonNull final String newClassName) {
        synchronized (lock) {
            if (globalSpringMainClassName != null && !globalSpringMainClassName.equals(newClassName)) {
                throw new IllegalArgumentException(String.format("Spring Main Class has to be the same for all Functions in FunctionApp (Current=%s / New =%s)",
                                                                 globalSpringMainClassName,
                                                                 newClassName));
            }

            log.info("Spring Main Class is " + newClassName);
            globalSpringMainClassName = newClassName;
        }
    }

    static void setSpringMainClassFromSystemPropertyIfNeeded() {
        synchronized (lock) {
            if (globalSpringMainClassName == null) {
                setSpringMainClassFromSystemProperty();
            }
        }
    }

    static void clearGlobalSpringMainClass() {
        synchronized (lock) {
            globalSpringMainClassName = null;
        }
    }

    static ApplicationContext getApplicationContext() {

        val applicationContext = ApplicationContextSingletonBean.getApplicationContext();

        if (applicationContext != null) {
            log.info(String.format("Existing ApplicationContext already (ID=%s).  Returning it", applicationContext.getId()));
            return applicationContext;
        }

        synchronized (lock) {
            if (applicationContext != null) {
                return applicationContext;
            }

            if (globalSpringMainClassName == null) {
                log.warn("Global Spring Main Class not set!  Reading from System Properties...");
                setSpringMainClassFromSystemProperty();
            }

            //  using messy introspection
            /*
            try {
                log.info(String.format("Loading Spring Context from %s", globalSpringMainClassName));

                val systemCL = ClassLoader.getSystemClassLoader();

                val applicationClass = systemCL.loadClass(globalSpringMainClassName);

                val springAppClass = systemCL.loadClass(SpringApplication.class.getName());
                val springAppCtor = springAppClass.getDeclaredConstructor(Class[].class);
                //  thanks God, for varargs, messing up with with introspection
                val springAppInst = springAppCtor.newInstance(new Object[] {new Class[] {applicationClass}});
                return (ApplicationContext) springAppClass.getMethod("run", String[].class).invoke(springAppInst, new Object[]{new String[]{}});
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
                throw new SpringFailureException(e);
            } catch (InvocationTargetException e) {
                throw new SpringFailureException((Exception)e.getTargetException());
            }
            */


            //  loading this will set the ApplicationContextSingleBean using the ApplicationContextAware on it.
            try {
                return new SpringApplication(Class.forName(globalSpringMainClassName)).run();
            } catch (ClassNotFoundException e) {
                throw new SpringFailureException(e);
            }
        }
    }

    static private boolean setSpringMainClassFromSystemProperty() {

        synchronized (lock) {
            val springMainClassName = Optional.ofNullable(System.getenv(SYSPROP_MAIN_CLASS))
                                              .filter(StringUtils::isNotBlank)
                                              .orElseGet(() -> System.getProperty(SYSPROP_MAIN_CLASS));
            if (StringUtils.isBlank(springMainClassName)) {
                //throw new SpringFailureException("Spring's context main class name missing (MAIN_CLASS=...)");
                log.warn(String.format("No value for %s!  Hope the Spring's Application class naem is set somewhere else.", SYSPROP_MAIN_CLASS));
                return false;
            }

            setGlobalSpringMainClassName(springMainClassName);
            return true;
        }
    }
}
