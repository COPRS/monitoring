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

package eu.csgroup.coprs.monitoring.traceingestor;

import eu.csgroup.coprs.monitoring.common.bean.ReloadableBeanFactory;
import eu.csgroup.coprs.monitoring.common.ingestor.EntityIngestor;
import eu.csgroup.coprs.monitoring.traceingestor.config.IngestionGroup;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


@EnableConfigurationProperties({ TraceIngestorProperties.class, IngestionGroup.class })
@Configuration
@Import({ EntityIngestor.class, ReloadableBeanFactory.class })
public class TraceIngestorConfiguration {

    @Bean(name = "trace-ingestor")
    public TraceIngestorSink traceIngestor(ReloadableBeanFactory reloadableBeanFactory, EntityIngestor entityIngestor) {
        return new TraceIngestorSink(reloadableBeanFactory, entityIngestor);
    }

}