package eu.csgroup.coprs.monitoring.tracefilter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.slf4j.LoggerFactory.getLogger;

import eu.csgroup.coprs.monitoring.tracefilter.rule.FilteringRule;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RunWith(SpringRunner.class)
@EnableConfigurationProperties(value = FilteringRule.class)
public class TraceFilterTests {

    private static final Logger LOGGER = getLogger(TraceFilterTests.class);

    private static final TraceFilterConfiguration conf = new TraceFilterConfiguration();

    @Autowired
    private FilteringRule rules;

    @Test
    public void testFilter() {
        System.out.println();
        System.out.println("####### res: " + rules.getRules().toString());
        System.out.println();
    }

    @Test
    public void testDeserializer() {
        final var processor = conf.traceFilter(conf.traceMapper(), rules);
        // Given
        /*final var mapper = Mappers.testMapper()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        System.out.println("1 " + mapper.isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
        System.out.println("1 " + mapper.isEnabled(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES));*/
        //final var logSink = config.logSink(mapper, traceService);
        final var content = getContent("trace.json");

        final var trace = processor.apply(toMessage(content));

        assertThat(trace).hasSize(1)
            /*.isEqualTo()*/;
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
