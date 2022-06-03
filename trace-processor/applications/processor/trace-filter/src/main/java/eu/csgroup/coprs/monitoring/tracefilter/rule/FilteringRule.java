package eu.csgroup.coprs.monitoring.tracefilter.rule;

import eu.csgroup.coprs.monitoring.tracefilter.yaml.YamlPropertySourceFactory;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.List;

@Data
@Configuration
@PropertySource(value = "${filter.ruleLocation:classpath:filter.yml}", factory = YamlPropertySourceFactory.class)
@ConfigurationProperties(prefix = "filter")
public class FilteringRule {
    List<Rule> rules;
}
