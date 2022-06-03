package eu.csgroup.coprs.monitoring.common.config;

import eu.csgroup.coprs.monitoring.common.datamodel.Trace;
import eu.csgroup.coprs.monitoring.common.jpa.TraceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;


/*@Service*/
@Configuration
@EnableJpaRepositories(basePackages = "com.csgroup.s2pdgs.common.jpa")
@EntityScan(basePackages = "com.csgroup.s2pdgs.common.datamodel")
@EnableTransactionManagement
public class TraceConfiguration {
    @Autowired
    private TraceRepository repository;

    public Iterable<Trace> list() {
        return repository.findAll();
    }

    public long add(Trace trace) {
        return repository.save(trace).getId();
    }
}
