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

package eu.csgroup.coprs.monitoring.common;

import eu.csgroup.coprs.monitoring.common.bean.BeanProperty;
import eu.csgroup.coprs.monitoring.common.properties.ReloadableYamlPropertySource;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

public class BeanPropertyTests {

    @Test
    public void testNominal() {
        // Given
        final var prop = new BeanProperty("log.trace.task.missing_output[filename_strings][2]");

        // When

        // Then
        assertThat(prop).extracting(BeanProperty::getRawPropertyPath).isEqualTo("log.trace.task.missing_output[filename_strings][2]");
        assertThat(prop).extracting(BeanProperty::getRawBeanPropertyPath).isEqualTo("trace.task.missing_output[filename_strings][2]");
        assertThat(prop).extracting(BeanProperty::getBeanPropertyPath).isEqualTo("trace.task.missingOutput[filename_strings][2]");
        assertThat(prop).extracting(beanProp -> beanProp.getBeanPropertyPath(true)).isEqualTo("Log.trace.task.missingOutput[filename_strings][2]");
        assertThat(prop).extracting(BeanProperty::getBeanName).isEqualTo("Log");

    }

    @Test
    public void testExtraction() {
        // Given
        final var beanProp = new ReloadableYamlPropertySource(
                "test",
                BeanPropertyTests.class.getClassLoader().getResource("beanProperties.yaml").getPath());
        final var expectedProperties = new HashMap<String, String>();
        expectedProperties.put("filters[0].name", "S2-AUX_DATA");
        expectedProperties.put("filters[0].rules.header.mission", "S2");
        expectedProperties.put("filters[0].rules.header.type", "REPORT");
        expectedProperties.put("filters[0].rules.task.event", "END");
        expectedProperties.put("filters[0].rules.task.status", "OK");
        expectedProperties.put("filters[0].rules.message.content", "End metadata extraction");
        expectedProperties.put("filters[0].rules[task.input[filename_strings][0]]", ".*_V.*");

        expectedProperties.put( "filters[1].name", "S2-DSIB");
        expectedProperties.put("filters[1].rules.header.mission", "S2");
        expectedProperties.put("filters[1].rules.header.type", "REPORT");
        expectedProperties.put("filters[1].rules.task.event", "END");
        expectedProperties.put("filters[1].rules.task.status", "OK");
        expectedProperties.put("filters[1].rules.message.content", "End processing of");
        expectedProperties.put("filters[1].rules[task.output[filename_strings][0]]", ".*_DSIB\\.xml");

        expectedProperties.put("filters[2].name", "S2-CHUNK");
        expectedProperties.put("filters[2].rules.header.mission", "S2");
        expectedProperties.put("filters[2].rules.header.type", "REPORT");
        expectedProperties.put("filters[2].rules.task.event", "END");
        expectedProperties.put("filters[2].rules.task.status", "OK");
        expectedProperties.put("filters[2].rules.message.content", "End processing of");
        expectedProperties.put("filters[2].rules[task.output[filename_strings][0]]", ".*ch2_DSDB.* |.*\\.raw");

        // When
        final var retrievedProperties = Arrays.stream(beanProp.getPropertyNames()).toList();
        final var retrievedValues = expectedProperties.keySet().stream().map(beanProp::getProperty).toList();


        assertThat(retrievedProperties).containsExactlyInAnyOrderElementsOf(expectedProperties.keySet());
        assertThat(retrievedValues).containsExactlyInAnyOrderElementsOf(expectedProperties.values());
    }

    @Test
    public void testOther () {
        final var prop = new BeanProperty("log.trace.task.missing_output[filename_strings][2]");
        final var prop2 = new BeanProperty("Log.trace.task.missing_output[filename_strings][2]");

        assertThat(prop)
                .isNotEqualTo(prop2)
                .hasToString(prop.toString());
    }
}
