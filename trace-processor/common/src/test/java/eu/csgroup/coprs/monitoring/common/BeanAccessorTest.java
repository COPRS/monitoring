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

import eu.csgroup.coprs.monitoring.common.bean.BeanAccessor;
import eu.csgroup.coprs.monitoring.common.bean.BeanProperty;
import eu.csgroup.coprs.monitoring.common.bean.InstantPropertyEditor;
import eu.csgroup.coprs.monitoring.common.datamodel.Header;
import eu.csgroup.coprs.monitoring.common.datamodel.Trace;
import eu.csgroup.coprs.monitoring.common.datamodel.TraceLog;
import org.junit.Test;
import org.springframework.beans.NullValueInNestedPathException;
import org.springframework.beans.PropertyAccessorFactory;

import java.time.Instant;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class BeanAccessorTest {
    @Test
    public void testGetProperty() {
        // Given
        final var trace = new TraceLog();
        var wrapper = PropertyAccessorFactory.forBeanPropertyAccess(trace);
        wrapper.registerCustomEditor(Instant.class, new InstantPropertyEditor());
        final var accessor = new BeanAccessor(wrapper);

        // When
        final var value = accessor.getPropertyValue(new BeanProperty("log.trace"));

        // Then
        assertThat(value).isNull();
    }

    @Test
    public void testGetCachedProperty() {
        // Given
        final var trace = new TraceLog();
        var wrapper = PropertyAccessorFactory.forBeanPropertyAccess(trace);
        wrapper.registerCustomEditor(Instant.class, new InstantPropertyEditor());
        final var accessor = new BeanAccessor(wrapper);

        // When
        // Cache retrieved value
        accessor.getPropertyValue(new BeanProperty("log.trace"));
        // Get cached value
        final var value = accessor.getPropertyValue(new BeanProperty("log.trace"));

        // Then
        assertThat(value).isNull();
    }

    @Test
    public void testSetProperty() {
        // Given
        final var trace = new TraceLog();
        var wrapper = PropertyAccessorFactory.forBeanPropertyAccess(trace);
        wrapper.registerCustomEditor(Instant.class, new InstantPropertyEditor());
        final var accessor = new BeanAccessor(wrapper);

        // When
        accessor.setPropertyValue(new BeanProperty("log.trace"), new Trace());
        final var value = accessor.getDelegate().getWrappedInstance();

        // Then
        assertThat((TraceLog)value).extracting(TraceLog::getTrace).isNotNull();
    }

    @Test
    public void testGetValueInNullObject () {
        // Given
        final var trace = new TraceLog();
        var wrapper = PropertyAccessorFactory.forBeanPropertyAccess(trace);
        wrapper.registerCustomEditor(Instant.class, new InstantPropertyEditor());
        final var accessor = BeanAccessor.from(wrapper);

        // When
        final var thrownException = assertThatThrownBy(() -> accessor.getPropertyValue(new BeanProperty("log.trace.task")));

        // Then
        thrownException.isNotNull().isInstanceOf(NullValueInNestedPathException.class);
    }

    @Test
    public void testSetInstant () {
        // Given
        final var traceLog = new TraceLog();
        final var trace = new Trace();
        final var header = new Header();
        trace.setHeader(header);
        traceLog.setTrace(trace);

        var wrapper = PropertyAccessorFactory.forBeanPropertyAccess(traceLog);
        wrapper.registerCustomEditor(Instant.class, new InstantPropertyEditor());
        final var accessor = new BeanAccessor(wrapper);

        // When
        final var instant = Instant.parse("2019-01-21T05:24:40.00Z");
        accessor.setPropertyValue(new BeanProperty("log.trace.header.timestamp"), instant);
        final var value = accessor.getDelegate().getWrappedInstance();

        // Then

        assertThat((TraceLog)value).extracting(traceLogCheck -> traceLogCheck.getTrace().getHeader().getTimestamp())
                .isNotNull()
                .isEqualTo(instant);
    }

    @Test
    public void testSetInstantAsString () {
        // Given
        final var traceLog = new TraceLog();
        final var trace = new Trace();
        final var header = new Header();
        trace.setHeader(header);
        traceLog.setTrace(trace);

        var wrapper = PropertyAccessorFactory.forBeanPropertyAccess(traceLog);
        wrapper.registerCustomEditor(Instant.class, new InstantPropertyEditor());
        final var accessor = new BeanAccessor(wrapper);

        // When
        accessor.setPropertyValue(new BeanProperty("log.trace.header.timestamp"), "2019-01-21T05:24:40.000000Z");
        final var value = accessor.getDelegate().getWrappedInstance();

        // Then
        final var res = Instant.parse("2019-01-21T05:24:40.00Z");
        assertThat((TraceLog)value).extracting(traceLogCheck -> traceLogCheck.getTrace().getHeader().getTimestamp())
                .isNotNull()
                .isEqualTo(res);
    }

    @Test
    public void testOther () {
        final var beanAccessor = new BeanAccessor(PropertyAccessorFactory.forBeanPropertyAccess(new TraceLog()));
        final var beanAccessor2 = new BeanAccessor(PropertyAccessorFactory.forBeanPropertyAccess(new TraceLog()));

        assertThat(beanAccessor)
                .isNotEqualTo(beanAccessor2);
        assertThat(beanAccessor.toString())
                .isNotEqualTo(beanAccessor2.toString());

    }
}
