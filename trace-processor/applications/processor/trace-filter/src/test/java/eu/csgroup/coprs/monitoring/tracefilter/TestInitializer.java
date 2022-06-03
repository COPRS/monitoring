package eu.csgroup.coprs.monitoring.tracefilter;

import org.junit.rules.ExternalResource;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

public class TestInitializer extends ExternalResource implements ApplicationContextInitializer<ConfigurableApplicationContext>, AutoCloseable {
    @Override
    public void close() throws Exception {

    }

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        TestPropertyValues.of(
            "filter.ruleLocation=classpath:filter.yml"
        ).applyTo(applicationContext.getEnvironment());
    }
}
