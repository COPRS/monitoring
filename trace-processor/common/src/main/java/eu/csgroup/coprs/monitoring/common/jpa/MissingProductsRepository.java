<<<<<<< HEAD
package eu.csgroup.coprs.monitoring.common.jpa;

import eu.csgroup.coprs.monitoring.common.datamodel.entities.MissingProducts;

//@Transactional
public interface MissingProductsRepository extends EntityRepository<MissingProducts, Long> {
}
||||||| b8aeece
=======
package eu.csgroup.coprs.monitoring.common.jpa;

import eu.csgroup.coprs.monitoring.common.datamodel.entities.ExternalInput;
import eu.csgroup.coprs.monitoring.common.datamodel.entities.MissingProducts;
import org.springframework.stereotype.Repository;

//@Transactional
@Repository
public interface MissingProductsRepository extends EntityRepository<MissingProducts, Long> {
}
>>>>>>> dev
