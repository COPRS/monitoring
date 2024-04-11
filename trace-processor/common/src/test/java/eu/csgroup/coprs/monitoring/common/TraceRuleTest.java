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

import com.fasterxml.jackson.databind.json.JsonMapper;
import eu.csgroup.coprs.monitoring.common.bean.BeanAccessor;
import eu.csgroup.coprs.monitoring.common.bean.BeanPropertyRuleGroup;
import eu.csgroup.coprs.monitoring.common.bean.InstantPropertyEditor;
import eu.csgroup.coprs.monitoring.common.datamodel.EndTask;
import eu.csgroup.coprs.monitoring.common.datamodel.Trace;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.springframework.beans.PropertyAccessorFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class TraceRuleTest {
    @Test
    public void testRuleWithEmtpyMissingOutput () throws Exception {
        // Given
        final var trace = loadTrace("trace-emptyMissingOutput.json");

        final var filter = new BeanPropertyRuleGroup();
        filter.setRules(Map.of("trace.task.missing_output[0]", ".+"));

        var wrapper = PropertyAccessorFactory.forBeanPropertyAccess(trace);
        wrapper.registerCustomEditor(Instant.class, new InstantPropertyEditor());
        final var accessor = BeanAccessor.from(wrapper);

        // When
        final var res = filter.test(accessor);

        // Then
        assertThat(res).isFalse();
    }

    @Test
    public void testRuleWithNullMissingOutput () throws Exception {
        // Given
        final var trace = loadTrace("trace-nullMissingOutput.json");

        final var filter = new BeanPropertyRuleGroup();
        filter.setRules(Map.of("trace.task.missing_output[0]", ".+"));

        var wrapper = PropertyAccessorFactory.forBeanPropertyAccess(trace);
        final var accessor = BeanAccessor.from(wrapper);

        // When
        final var res = filter.test(accessor);

        // Then
        assertThat(res).isFalse();
    }

    @Test
    public void testRuleWithMissingOutput () throws Exception {
        // Given
        final var trace = loadTrace("trace-end.json");

        final var filter = new BeanPropertyRuleGroup();
        filter.setRules(Map.of("trace.task.missing_output[0]", ".+"));

        var wrapper = PropertyAccessorFactory.forBeanPropertyAccess(trace);
        final var accessor = BeanAccessor.from(wrapper);

        // When
        final var res = filter.test(accessor);

        // Then
        assertThat(res).isTrue();
    }

    // -- Helper -- //

    protected Trace loadTrace(String classpathResource, JsonMapper mapper) throws IOException{
        final var content = getContent(classpathResource);

        return mapper.readValue(content, Trace.class);
    }

    protected Trace loadTrace(String classpathResource) throws IOException {
        final var mapper = JsonMapper.builder()
                .findAndAddModules()
                .build();

        return loadTrace(classpathResource, mapper);
    }

    protected String getContent(String classpathResource) {
        try {
            return IOUtils.resourceToString(classpathResource, StandardCharsets.UTF_8, TraceDeserializerTests.class.getClassLoader());
        } catch (IOException e) {
            throw new RuntimeException("Cannot retrieve content of resource %s".formatted(classpathResource), e);
        }
    }
}

