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

package eu.csgroup.coprs.monitoring.tracefilter;


import java.util.List;
import java.util.function.Function;

import com.fasterxml.jackson.databind.SerializationFeature;
import eu.csgroup.coprs.monitoring.common.bean.ReloadableBeanFactory;
import eu.csgroup.coprs.monitoring.common.message.FilteredTrace;
import eu.csgroup.coprs.monitoring.tracefilter.json.JsonValidator;
import eu.csgroup.coprs.monitoring.tracefilter.rule.FilterGroup;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.Message;

@Configuration
@EnableConfigurationProperties({TraceFilterProperties.class, FilterGroup.class})
@Import({JsonValidator.class, ReloadableBeanFactory.class})
public class TraceFilterConfiguration {

    @Bean
    public ObjectMapper traceMapper () {
        return JsonMapper.builder()
                .findAndAddModules()
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .build();
    }

    @Bean(name = "trace-filter")
    public Function<Message<String>, List<Message<FilteredTrace>>> traceFilter(JsonValidator jsonMapper, ReloadableBeanFactory factory) {
       return new TraceFilterProcessor(jsonMapper, factory);
    }

}
