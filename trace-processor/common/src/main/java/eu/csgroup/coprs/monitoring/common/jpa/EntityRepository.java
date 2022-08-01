package eu.csgroup.coprs.monitoring.common.jpa;

import eu.csgroup.coprs.monitoring.common.datamodel.entities.DefaultEntity;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

@NoRepositoryBean
public interface EntityRepository<T extends DefaultEntity> extends JpaRepository<T, Long>, JpaSpecificationExecutor<T>  {
    //@Query("select e from #{#entityName} e where e.?#{[0]} = ?#{[1]}")
    //public Iterable<T> findAll(String fieldName, String fieldValue);

    @Override
    List<T> findAll(Specification<T> spec);

}
