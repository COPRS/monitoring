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

package eu.csgroup.coprs.monitoring.traceingestor.config;

import eu.csgroup.coprs.monitoring.common.properties.PropertyUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Configuration class to define a set of mapping to create entities from a trace.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Ingestion {
    /**
     * Configuration name
     */
    private String name;
    /**
     * Mapping list to define where trace value must be set in which entity and field
     */
    private List<Mapping> mappings;
    /**
     * Map where key define the alias name and value define restriction on which entity
     */
    private Map<String, Alias> alias = new HashMap<>();

    /**
     * List of SQL query to use under certain condition to process duplicate processing.
     */
    private List<DuplicateProcessing> duplicateProcessings;

    /**
     * Set alias by modifying key to be in pascal case
     *
     * @param associations Alias mapping
     */
    public void setAlias(Map<String, Alias> associations) {
        this.alias = associations.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        entry -> PropertyUtil.snake2PascalCasePropertyName(entry.getKey()),
                        Map.Entry::getValue)
                );
    }
}
