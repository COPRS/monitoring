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

package eu.csgroup.coprs.monitoring.traceingestor.processor;

import eu.csgroup.coprs.monitoring.common.datamodel.entities.DefaultEntity;
import eu.csgroup.coprs.monitoring.common.ingestor.EntityMetadata;
import eu.csgroup.coprs.monitoring.traceingestor.config.Mapping;
import lombok.Data;

import java.util.*;

/**
 * Describe the process to run by declaring the entity type that will be created (given by {@link EntityMetadata} instance)
 * and the relation with the other process to run (used to order the execution of the process).
 */
@Data
public class ProcessorDescription {
    /**
     * Processor name
     */
    private String name;

    private EntityMetadata entityMetadata;
    /**
     * Key: entity class
     * Value: collection of processor name handling entity creation
     */
    private Map<Class<? extends DefaultEntity>, Collection<String>> relyOnProc = new HashMap<>();

    /**
     * Name of the configuration where mapping list is issued
     */
    private String configurationName;

    /**
     * Mapping configuration list
     */
    private List<Mapping> mappings;

    /**
     * Set on which processor this one rely on and to which entity type it's associated
     * (cf. {@link EntityMetadata#getRelyOn()})
     *
     * @param entityClass entity type name of the relation
     * @param procsName processor description name (which are an entity type name or an alias name)
     */
    public void putRelyOnProcs (Class<? extends DefaultEntity> entityClass, Collection<String> procsName) {
        relyOnProc.put(entityClass, procsName);
    }
}