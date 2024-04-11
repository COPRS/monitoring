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

package eu.csgroup.coprs.monitoring.tracefilter.rule;

import eu.csgroup.coprs.monitoring.common.bean.BeanAccessor;
import eu.csgroup.coprs.monitoring.common.properties.ReloadableYamlPropertySourceFactory;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

@Data
@Configuration
@PropertySource(name = "filterGroup", value = "${filter.path}", factory = ReloadableYamlPropertySourceFactory.class)
@ConfigurationProperties()
@Slf4j
public class FilterGroup implements Function<BeanAccessor, Optional<Filter>> {
    List<Filter> filters;

    @Override
    public Optional<Filter> apply(BeanAccessor node) {
        final Predicate<Filter> checkFilter = filter -> filter.test(node);

        return filters
                .stream()
                .map(filter -> {
                    log.trace("Apply filter %s".formatted(filter.getName()));
                    return filter;
                })
                .filter(checkFilter)
                .findFirst();
    }
}
