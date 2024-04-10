/*
 * Copyright 2023 CS Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.csgroup.coprs.monitoring.traceingestor.association;

import eu.csgroup.coprs.monitoring.common.bean.BeanProperty;
import eu.csgroup.coprs.monitoring.common.ingestor.EntityFinder;
import eu.csgroup.coprs.monitoring.common.properties.PropertyUtil;
import eu.csgroup.coprs.monitoring.common.ingestor.EntityHelper;
import eu.csgroup.coprs.monitoring.traceingestor.entity.EntityProcessing;
import eu.csgroup.coprs.monitoring.traceingestor.entity.EntityState;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Field;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

/**
 * Default association instance
 */
@RequiredArgsConstructor
public class DefaultAssociation implements EntityAssociation {

    private final Deque<Field> associationFields;

    /**
     * Create relation with each references by using entity container and creating copy for the others.
     *
     * @param entityContainer Entity in which to associate a reference and create a copy for the others.
     * @param references Reference list for which to create association with entity container.
     * @param entityFinder Instance that will process search in database.
     * @return All copy of entity container (including original)
     */
    public List<EntityProcessing> associate(EntityProcessing entityContainer, List<EntityProcessing> references, EntityFinder entityFinder) {
        final var associatedEntities = new LinkedList<EntityProcessing>();

        // Use original entity container for the first association
        boolean copy = false;

        final var entIt = references.iterator();
        EntityProcessing currentIt;

        while (entIt.hasNext()) {
            currentIt = entIt.next();

            associatedEntities.add(associate(entityContainer, currentIt, copy));

            // Then create a copy of the entity container.
            copy = true;
        }

        return associatedEntities;
    }

    protected  EntityProcessing associate(EntityProcessing entityContainer, EntityProcessing reference, boolean copy) {
        try {
            // Create copy or use the original.
            EntityProcessing containerRef;
            if(copy) {
                containerRef = EntityProcessing.fromEntity(EntityHelper.copy(entityContainer.getEntity()), EntityState.NEW);
            } else {
                containerRef = entityContainer;
            }

            // If association field size is greater than 1 it means that we are associating a reference
            final var iter = associationFields.iterator();

            // Construct path to set reference entity in container entity for chained field
            // The value "entity" is arbitrary and does not have importance (represent container entity level)
            var path = "entity";
            while (iter.hasNext()) {
                path = PropertyUtil.getPath(path, iter.next().getName());
            }

            // Set reference entity in container entity
            containerRef.setPropertyValue(new BeanProperty(path), reference.getEntity());
            return containerRef;
        } catch (Exception e) {
            throw new AssociationException(
                    "Cannot associate entity reference %s to entity container %s".formatted(
                            reference.getClass().getSimpleName(),
                            entityContainer.getClass().getSimpleName()
                    ), e
            );
        }
    }
}