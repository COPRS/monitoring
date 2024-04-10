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

package eu.csgroup.coprs.monitoring.traceingestor.converter;

import org.springframework.beans.ConversionNotSupportedException;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Class that handle the match action (only match one value at a time).<br>
 * <br>
 * Order and definition of required arguments is the following:
 * <ul>
 *  <li>STATIC: Pattern to match</li>
 *  <li>DYNAMIC: value to match to</li>
 * </ul>
 * <br><br>
 */
public class MatchAction extends Action {
    public MatchAction(String rawAction) {
        super(rawAction);
    }

    @Override
    protected List<Action.ARG_TYPE> getArgsMapping() {
        return List.of(Action.ARG_TYPE.STATIC, Action.ARG_TYPE.DYNAMIC);
    }

    @Override
    protected String getRequiredAction() {
        return ActionConstant.MATCH_ACTION_PATTERN;
    }

    @Override
    public Object execute(List<Object> values) {
        final var matcher = Pattern.compile(getAllArgs().get(0));
        final var value = values.get(0);

        try {
            return match(matcher, value) ? value : null;
        } catch (ConversionNotSupportedException | ClassCastException e) {
            // If format failed it may be caused by the value which is a list of value
            if (value instanceof final Collection<?> collection) {
                // Handle case where value is a list
                return collection.stream()
                        .map(val -> match(matcher, (String) val) ? val : null)
                        .filter(Objects::nonNull)
                        .toList();
            } else {
                throw e;
            }
        }

    }
    private boolean match(Pattern match, Object value) {
        if (value != null && match != null) {
            return match(match, (String)value);
        } else {
            return true;
        }
    }

    private boolean match(Pattern match, String value) {
        final var matcher = match.matcher(value);

        return matcher.find();
    }
}
