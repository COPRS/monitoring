package eu.csgroup.coprs.monitoring.tracefilter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.slf4j.LoggerFactory.getLogger;

import eu.csgroup.coprs.monitoring.tracefilter.json.JsonValidator;
import eu.csgroup.coprs.monitoring.tracefilter.rule.FilterGroup;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
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

    private static final Logger LOGGER = getLogger(TraceFilterTests.class);

    private static final TraceFilterConfiguration conf = new TraceFilterConfiguration();

    @Autowired
    private FilterGroup rules;

    @Autowired
    private JsonValidator jsonValidator;

    @Test
    public void testFilter() {
        System.out.println();
        System.out.println("####### res: " + rules.toString());
        System.out.println();
    }

    @Test
    public void testNominal () {
        final var processor = conf.traceFilter(jsonValidator, rules);
        // Given
        /*final var mapper = Mappers.testMapper()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        System.out.println("1 " + mapper.isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
        System.out.println("1 " + mapper.isEnabled(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES));*/
        //final var logSink = config.logSink(mapper, traceService);
        final var content = getContent("trace.json");

        // When
        //final var startDate = Instant.now();
        //IntStream.range(0, 10001).forEach(count -> {
            final var trace = processor.apply(toMessage(content));
        //});
        //final var endDate = Instant.now();

        //System.out.println(Duration.between(startDate, endDate).toSeconds());

        // Then
        assertThat(trace).hasSize(1)
                .map(t -> t.getPayload().getRuleName())
                .allMatch(n -> n.equals("filter-2"));
    }

    @Test
    public void testPartial () {
        // Given
        final var processor = conf.traceFilter(jsonValidator, rules);
        final var content = getContent("trace-partial.json");

        // When
        final var assertion = assertThatThrownBy(() -> processor.apply(toMessage(content)));

        // Then
        assertion.isNotNull();
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
