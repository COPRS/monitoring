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

package eu.csgroup.coprs.monitoring.common.jpa;

import eu.csgroup.coprs.monitoring.common.datamodel.entities.DefaultEntity;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collection;

public class EntitySpecification {

    private EntitySpecification () {

    }

    public static <T extends DefaultEntity> Specification<T> getEntityBy (String attributeName, Object attributeValue) {
        return (root, query, criteriaBuilder) -> {
            if (attributeValue instanceof Collection<?>) {
                return root.get(attributeName).in((Collection<?>) attributeValue);
            } else {
                return criteriaBuilder.equal(root.get(attributeName), attributeValue);
            }
        };
    }
}