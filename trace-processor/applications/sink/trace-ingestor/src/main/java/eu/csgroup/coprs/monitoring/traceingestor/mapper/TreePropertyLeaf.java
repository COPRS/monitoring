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

package eu.csgroup.coprs.monitoring.traceingestor.mapper;

import eu.csgroup.coprs.monitoring.common.bean.BeanProperty;
import eu.csgroup.coprs.monitoring.traceingestor.config.Mapping;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@EqualsAndHashCode()
public class TreePropertyLeaf implements TreeProperty {
    private final Mapping rule;

    private final Map<BeanProperty,Object> rawValues = new HashMap<>();

    public TreePropertyLeaf(Mapping rule) {
        this.rule = rule;
    }

    public void putRawValue (BeanProperty beanProperty, Object rawValue) {
        rawValues.put(beanProperty, rawValue);
    }

    public TreePropertyLeaf copy () {
        final var newLeaf = new TreePropertyLeaf(this.rule);

        newLeaf.rawValues.putAll(this.rawValues);

        return newLeaf;
    }
}
