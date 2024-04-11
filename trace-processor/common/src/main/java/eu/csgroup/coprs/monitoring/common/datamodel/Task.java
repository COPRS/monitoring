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

import com.fasterxml.jackson.annotation.*;
import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Map;


@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "event", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = BeginTask.class, name = "BEGIN"),
        @JsonSubTypes.Type(value = EndTask.class, name = "END") }
)
public class Task {
    @NotNull
    @Pattern(regexp = Properties.UID_REGEX, message = "task.uid does not match UID pattern")
    private String uid;

    @NotNull
    @Size(max = 256, message = "Task name cannot exceed 256 characters")
    private String name;

    @NotNull
    private Event event;

    @JsonProperty("data_rate_mebibytes_sec")
    private double dataRateMebibytesSec;
    @JsonProperty("data_volume_mebibytes")
    private double dataVolumeMebibytes;

    private String satellite;

    @NotNull
    private Map<String, Object> input;
}