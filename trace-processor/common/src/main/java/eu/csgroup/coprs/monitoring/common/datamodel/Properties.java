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

package eu.csgroup.coprs.monitoring.common.datamodel;

public final class Properties {
    public static final String UID_REGEX = "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}";
    public static final String VERSION_REGEX = "[0-9]*\\.[0-9]*\\.[0-9]*-?[a-zA-Z0-9]*";

    public static final String DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'";
    public static final String DEFAULT_TIMEZONE= "UTC";
    public static final String TRACE_LOG_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS'Z'";
    public static final int STRING_FIELD_256_LIMIT = 256;
    public static final int STRING_FIELD_10K_LIMIT = 10000;
}
