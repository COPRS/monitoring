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
 * Constants used in action class
 */
public class ActionConstant {
    private ActionConstant () {

    }

    /**
     * Separator used between each argument (and also action name and the first argument)
     */
    public static final String ARGS_SEPARATOR = " ";

    /**
     * Character used to encapsulate argument containing whitespace
     */
    public static final char ARG_ENCAPSULATION = '"';

    /**
     * Character to use to indicate that this is not a {@link #ARG_ENCAPSULATION}
     */
    public static final char ESCAPE_DELIMITER = '\\';

    /**
     * Default signature of the base action
     */
    public static final String DEFAULT_ACTION_PATTERN = "ACTION_NAME <arg>...";

    /**
     * Signature of the format action
     */
    public static final String FORMAT_ACTION_PATTERN = "FORMAT <pattern> <formatter> <value>";

    /**
     * Signature of the match action
     */
    public static final String MATCH_ACTION_PATTERN = "MATCH <pattern> <value>";

    /**
     * Signature of the subtract action
     */
    public static final String SUBSTRACT_ACTION_PATTERN = "SUBSTRACT <value1> <value2>...";
}
