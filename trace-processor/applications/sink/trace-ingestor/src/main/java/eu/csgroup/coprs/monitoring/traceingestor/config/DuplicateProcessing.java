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

import eu.csgroup.coprs.monitoring.common.bean.BeanPropertyRuleGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Configuration class to define SQL query to use to process duplicate processing entity when a set of rules are validated
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DuplicateProcessing extends BeanPropertyRuleGroup {
    /**
     * SQL query start point to find duplicate entity (recursive)
     */
    private String query;

}
