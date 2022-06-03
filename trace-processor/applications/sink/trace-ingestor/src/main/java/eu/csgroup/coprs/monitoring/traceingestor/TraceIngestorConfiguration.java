package eu.csgroup.coprs.monitoring.traceingestor;

import eu.csgroup.coprs.monitoring.common.config.TraceConfiguration;
import org.slf4j.Logger;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static org.slf4j.LoggerFactory.getLogger;

@EnableConfigurationProperties({ TraceIngestorProperties.class })
@Configuration
@Import({ TraceConfiguration.class})
public class TraceIngestorConfiguration {

    private static final Logger LOGGER = getLogger(TraceIngestorConfiguration.class);

    @Bean
    public TraceIngestorSink logSink(TraceConfiguration traceService) {
        return new TraceIngestorSink(traceService);
    }

}
