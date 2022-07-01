package eu.csgroup.coprs.monitoring.common.config;

import eu.csgroup.coprs.monitoring.common.datamodel.entities.AuxData;
import eu.csgroup.coprs.monitoring.common.datamodel.entities.Chunk;
import eu.csgroup.coprs.monitoring.common.datamodel.entities.Dsib;
import eu.csgroup.coprs.monitoring.common.datamodel.entities.ExternalInput;
import eu.csgroup.coprs.monitoring.common.jpa.AuxDataRepository;
import eu.csgroup.coprs.monitoring.common.jpa.EntityRepository;
import eu.csgroup.coprs.monitoring.common.jpa.ExternalInputRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.List;


/*@Service*/
@Configuration
@EnableJpaRepositories(basePackages = "eu.csgroup.coprs.monitoring.common.jpa")
@EntityScan(basePackages = "eu.csgroup.coprs.monitoring.common.datamodel.entities")
@EnableTransactionManagement
public class TraceConfiguration {
    @Autowired
    private ExternalInputRepository eiRepository;

    @Autowired
    private EntityRepository<Dsib> dRepository;

    @Autowired
    private EntityRepository<Chunk> cRepository;

    @Autowired
    private AuxDataRepository adRepository;

    public Iterable<ExternalInput> list() {
        return eiRepository.findAll();
    }

    public ExternalInput add(ExternalInput entity) {
        return eiRepository.save(entity);
    }

    public AuxData add(AuxData entity) {
        final var ei = eiRepository.findByFilename(entity.getFilename());
        if (ei == null) {
            return adRepository.save(entity);
        } else {
            entity.setId(ei.getId());
            return adRepository.save(entity);
        }
    }

    public Dsib add(Dsib entity) {
        final var ei = eiRepository.findByFilename(entity.getFilename());
        if (ei == null) {
            return dRepository.save(entity);
        } else {
            entity.setId(ei.getId());
            return dRepository.save(entity);
        }
    }

    public Chunk add(Chunk entity) {
        final var ei = eiRepository.findByFilename(entity.getFilename());
        if (ei == null) {
            return cRepository.save(entity);
        } else {
            entity.setId(ei.getId());
            return cRepository.save(entity);
        }
    }

    public Iterable<ExternalInput> addAll(Iterable<ExternalInput> entities){
        return eiRepository.saveAll(entities);
    }

    /*public ExternalInput get(String filename) {
        return repository.findBy();
    }*/
}
