package eu.csgroup.coprs.monitoring.common.datamodel;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import eu.csgroup.coprs.monitoring.common.json.PropertyNames;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TraceLog {

    @JsonProperty("@timestamp")
    private double timestamp;

    //@JsonFormat(shape=JsonFormat.Shape.STRING, pattern= PropertyNames.TRACE_LOG_TIME_PATTERN, timezone = PropertyNames.DEFAULT_TIMEZONE)
    private String time;

    private String stream;

    @JsonProperty("_p")
    private String p;

    @Valid
    @JsonProperty("log")
    private Trace trace;

    @NotNull
    private Map kubernetes;
}
