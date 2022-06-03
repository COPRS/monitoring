package eu.csgroup.coprs.monitoring.tracefilter;


import java.util.List;
import java.util.function.Function;

import eu.csgroup.coprs.monitoring.common.message.FilteredTrace;
import eu.csgroup.coprs.monitoring.tracefilter.rule.FilteringRule;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

@Configuration
@EnableConfigurationProperties({TraceFilterProperties.class, FilteringRule.class})
public class TraceFilterConfiguration {

    @Bean
    public ObjectMapper traceMapper () {
        return JsonMapper.builder()
            .findAndAddModules()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .build();
    }

    @Bean
    public Function<Message<String>, List<Message<FilteredTrace>>> traceFilter(ObjectMapper jsonMapper, FilteringRule filters) {
       return new TraceFilterProcessor(jsonMapper, filters);
    }

}
