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

package eu.csgroup.coprs.monitoring.common.properties;

import lombok.Getter;
import org.apache.commons.configuration2.YAMLConfiguration;

import java.util.Map;

/**
 * Workaround class to access to raw configuration map because {@link YAMLConfiguration} given access to a tree
 * associated to the configuration file without indicating if it's represented as a list in the configuration file
 * (needed when spring boot create configuration file where path must contain '[' and ']' to represent a list object)
 */
public class CustomYamlConfiguration extends YAMLConfiguration {
    /**
     * Raw structure of configuration file
     */
    @Getter
    private Map<String, Object> cache;


    @Override
    protected void load(Map<String, Object> map) {
        this.cache = map;
        super.load(map);
    }
}