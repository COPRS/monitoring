package eu.csgroup.coprs.monitoring.tracefilter;

import static org.assertj.core.api.Assertions.assertThat;

import eu.csgroup.coprs.monitoring.common.bean.ReloadableBeanFactory;
import eu.csgroup.coprs.monitoring.tracefilter.json.JsonValidator;
import eu.csgroup.coprs.monitoring.tracefilter.rule.FilterGroup;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RunWith(SpringRunner.class)
@EnableConfigurationProperties(FilterGroup.class)
@Import(TraceFilterConfiguration.class)
@ContextConfiguration(initializers = TestInitializer.class)
public class TraceFilterTests {

    private static final TraceFilterConfiguration conf = new TraceFilterConfiguration();

    @Autowired
    private JsonValidator jsonValidator;

    @Autowired
    private ReloadableBeanFactory factory;


    @Test
    public void testNominal () {
        final var processor = conf.traceFilter(jsonValidator, factory);
        // Given
        final var content = getContent("trace.json");

        // When
        final var trace = processor.apply(toMessage(content));

        // Then
        assertThat(trace).hasSize(1)
                .map(t -> t.getPayload().getRuleName())
                .allMatch(n -> n.equals("filter-2"));
    }

    @Test
    public void testPartial () {
        // Given
        final var processor = conf.traceFilter(jsonValidator, factory);
        final var content = getContent("trace-partial.json");

        // When
        final var trace = processor.apply(toMessage(content));

        // Then
        assertThat(trace).isEmpty();
    }

    protected static String getContent(String classpathResource) {
        try {
            return IOUtils.resourceToString(classpathResource, StandardCharsets.UTF_8, TraceFilterTests.class.getClassLoader());
        } catch (IOException e) {
            throw new RuntimeException("Cannot retrieve content of resource %s".formatted(classpathResource), e);
        }
    }

    private Message<String> toMessage(String jsonContent) {
        return new GenericMessage<>(jsonContent);
    }

}
