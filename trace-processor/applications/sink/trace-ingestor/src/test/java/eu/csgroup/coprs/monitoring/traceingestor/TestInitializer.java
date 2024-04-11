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

import org.junit.rules.ExternalResource;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

public class TestInitializer extends ExternalResource implements ApplicationContextInitializer<ConfigurableApplicationContext>, AutoCloseable {
    @Override
    public void close() throws Exception {

    }

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        try {
            TestPropertyValues.of(
                    "ingestion.path=classpath:ingestion.yaml"
            ).applyTo(applicationContext.getEnvironment());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
