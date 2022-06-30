package eu.csgroup.coprs.monitoring.common.datamodel;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import eu.csgroup.coprs.monitoring.common.json.PropertyNames;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Header {

    @NotNull
    private TraceType type;

    @NotNull
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern= PropertyNames.DATE_PATTERN, timezone = PropertyNames.DEFAULT_TIMEZONE)
    private Instant timestamp;

    @NotNull
    private TraceLevel level;

    @NotNull
    private Mission mission;

    private Workflow workflow;

    @JsonProperty("debug_mode")
    Boolean debugMode;

    @JsonProperty("tag_list")
    List<String> tagList;
}
