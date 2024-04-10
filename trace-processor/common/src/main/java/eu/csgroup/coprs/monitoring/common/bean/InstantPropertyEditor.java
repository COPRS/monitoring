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

import eu.csgroup.coprs.monitoring.common.datamodel.Properties;

import java.awt.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Editor that allow the use of string to set date in bean handled by a {@link BeanAccessor} or directly by a
 * {@link org.springframework.beans.BeanWrapper}
 */
public class InstantPropertyEditor implements PropertyEditor {
    private Instant instant;

    private final DateTimeFormatter formatter;

    /**
     * Create an editor with the default {@link Properties#DATE_PATTERN} date format
     */
    public InstantPropertyEditor () {
        this(Properties.DATE_PATTERN, Properties.DEFAULT_TIMEZONE);
    }

    /**
     * Create an editor with a specific date format
     *
     * @param datePattern date format
     * @param timeZone time zone
     */
    public InstantPropertyEditor (String datePattern, String timeZone) {
        formatter = DateTimeFormatter.ofPattern(datePattern)
                .withZone(ZoneId.of(timeZone));
    }

    @Override
    public void setValue(Object value) {
        if (value == null) {
            instant = null;
        } else if (value instanceof Instant castInstant) {
            instant = castInstant;
        }
    }

    @Override
    public Object getValue() {
        return instant;
    }

    @Override
    public boolean isPaintable() {
        return false;
    }


    @Override
    public void paintValue(Graphics gfx, Rectangle box) {
        // Intentionally empty because unused
    }

    @Override
    public String getJavaInitializationString() {
        return null;
    }

    @Override
    public String getAsText() {
        return formatter.format(instant);
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        instant = Instant.parse(text);
    }

    @Override
    public String[] getTags() {
        return new String[0];
    }

    @Override
    public Component getCustomEditor() {
        return null;
    }

    @Override
    public boolean supportsCustomEditor() {
        return false;
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        // Intentionally empty because unused
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        // Intentionally empty because unused
    }
}