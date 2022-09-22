package eu.csgroup.coprs.monitoring.traceingestor.association;

import eu.csgroup.coprs.monitoring.common.datamodel.entities.DefaultEntity;
import eu.csgroup.coprs.monitoring.common.ingestor.EntityFinder;

import java.util.List;

public interface EntityAssociation<C extends DefaultEntity, R extends DefaultEntity> {
    List<C> associate(C entityContainer, List<R> references, EntityFinder entityFinder);
}
