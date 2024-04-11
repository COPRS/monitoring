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

package eu.csgroup.coprs.monitoring.common.bean;

import eu.csgroup.coprs.monitoring.common.properties.PropertyUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class BeanProperty {
    /**
     * Snake case
     */
    @ToString.Include
    @EqualsAndHashCode.Include
    private final String rawPropertyPath;
    /**
     * Raw bean property path without bean name (same as {@link #rawPropertyPath} without first part of the path)<br>
     * <br>
     * Snake case
     */
    private String rawBeanPropertyPath;

    /**
     * Bean name (first part of {@link #rawPropertyPath} converted in pascale case)
     */
    private String beanName;

    /**
     * Same as {@link #rawBeanPropertyPath} but converted in Camel case
     */
    private String beanPropertyPath;

    /**
     * @deprecated
     * last part of the path (in snake case)
     */
    @Deprecated(forRemoval = true)
    private String propertyName;


    public BeanProperty (String property) {
        this.rawPropertyPath = property;
        convert();
    }

    /**
     * Extract all informations from the given path (bean name, path without bean name)
     */
    private void convert () {
        final var splittedPath = PropertyUtil.splitPath(rawPropertyPath, 2);
        beanName = PropertyUtil.snake2PascalCasePropertyName(splittedPath[0]);
        rawBeanPropertyPath = splittedPath[1];
        beanPropertyPath = PropertyUtil.snake2CamelCasePath(rawBeanPropertyPath);

        propertyName = rawBeanPropertyPath;
        while (propertyName.contains(PropertyUtil.INDEX_START)) {
            propertyName = propertyName.substring(
                    propertyName.indexOf(PropertyUtil.INDEX_START) + 1,
                    propertyName.indexOf(PropertyUtil.INDEX_END)
            );
        }

        int lastDelimiter = propertyName.lastIndexOf(PropertyUtil.PROPERTY_DELIMITER);
        if (lastDelimiter != -1) {
            propertyName = propertyName.substring(lastDelimiter + 1);
        }
    }

    public String getBeanPropertyPath() {
        return getBeanPropertyPath(false);
    }

    public String getBeanPropertyPath (boolean withBeanName) {
        if (withBeanName) {
            return PropertyUtil.getPath(beanName, beanPropertyPath);
        } else {
            return beanPropertyPath;
        }
    }
}
