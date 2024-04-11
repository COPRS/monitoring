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

/**
 * Factory used to retrieve instance associated to the desired action among:
 * <ul>
 *     <li>MATCH: Check if a value match the regex</li>
 *     <li>FORMAT: Check if a value match the regex and create a new value based on capturing group</li>
 *     <li>SUBSTRACT:subtract two value</li>
 * </ul>
 */
public class ActionFactory {
    public static final String MATCH_ACTION = "MATCH";
    public static final String FORMAT_ACTION = "FORMAT";

    public static final String SUBSTRACT_ACTION = "SUBSTRACT";

    private ActionFactory() {

    }

    /**
     * Find and create instance associated to desired action
     *
     * @param rawAction the desired action
     * @return instance associated to desired action otherwise throw an exception if action is unknown
     * @throws InvalidActionException thrown when action is unknown
     */
    public static Action getConverter(String rawAction) {
        if (rawAction.startsWith(MATCH_ACTION)) {
            return new MatchAction(rawAction);
        } else if (rawAction.startsWith(FORMAT_ACTION)) {
            return new FormatAction(rawAction);
        } else if (rawAction.startsWith(SUBSTRACT_ACTION)) {
            return new SubstractAction(rawAction);
        } else {
            throw new InvalidActionException ("Action %s not found".formatted(rawAction));
        }
    }
}
