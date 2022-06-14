package eu.csgroup.coprs.monitoring.tracefilter;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "trace-filter")
public class TraceFilterProperties {
    private String ruleLocation;
}
