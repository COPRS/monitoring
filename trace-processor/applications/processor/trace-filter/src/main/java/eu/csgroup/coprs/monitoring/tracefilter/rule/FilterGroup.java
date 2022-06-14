package eu.csgroup.coprs.monitoring.tracefilter.rule;

import com.fasterxml.jackson.databind.JsonNode;
import eu.csgroup.coprs.monitoring.common.properties.ReloadableYamlPropertySourceFactory;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Data
@Configuration
@PropertySource(name = "rules", value = "${filter.path}", factory = ReloadableYamlPropertySourceFactory.class)
@ConfigurationProperties()
@Slf4j
public class FilterGroup implements Function<JsonNode, Optional<Filter>> {
    List<Filter> filters;

    @Override
    public Optional<Filter> apply(JsonNode node) {
        return filters
                .stream()
                .filter(f -> {
                    log.trace("Apply filter %s".formatted(f.getName()));
                    return f.test(node);
                })
                .findFirst();
    }
}
