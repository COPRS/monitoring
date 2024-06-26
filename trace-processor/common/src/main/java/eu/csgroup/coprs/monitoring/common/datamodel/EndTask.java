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

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class EndTask extends Task {
    @NotNull
    private Status status;

    @NotNull
    @JsonProperty("error_code")
    private int errorCode;

    @NotNull
    @JsonProperty("duration_in_seconds")
    private Double durationInSeconds;

    @NotNull
    private Map<String, Object> output;

    @NotNull
    private Map<String, Object> quality;

    @JsonProperty("missing_output")
    private Collection<Object> missingOutput;
}