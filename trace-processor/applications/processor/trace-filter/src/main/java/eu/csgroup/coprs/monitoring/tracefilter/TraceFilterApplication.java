package eu.csgroup.coprs.monitoring.tracefilter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@SpringBootApplication(exclude = { ElasticsearchDataAutoConfiguration.class })
@ComponentScan(basePackages = "com.csgroup.s2pdgs.commonutils.probes")
@Import(TraceFilterConfiguration.class)
public class TraceFilterApplication {

    public static void main(String[] args) {
        SpringApplication.run(TraceFilterApplication.class, args);
    }

}
