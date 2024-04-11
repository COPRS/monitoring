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

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Header {

    @NotNull
    private TraceType type;

    @NotNull
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern= Properties.DATE_PATTERN, timezone = Properties.DEFAULT_TIMEZONE)
    private Instant timestamp;

    @NotNull
    private Level level;

    @NotNull
    private String mission;

    @JsonProperty("rs_chain_name")
    @Size(max= Properties.STRING_FIELD_256_LIMIT, message="header.rs_chain_name cannot exceed " + Properties.STRING_FIELD_10K_LIMIT + " characters")
    private String rsChainName;

    @JsonProperty("rs_chain_version")
    @Pattern(regexp = Properties.VERSION_REGEX, message = "header.rs_chain_version does not match UID pattern")
    private String rsChainVersion;

    private Workflow workflow;

    @JsonProperty("debug_mode")
    Boolean debugMode;

    @JsonProperty("tag_list")
    List<String> tagList;
}
