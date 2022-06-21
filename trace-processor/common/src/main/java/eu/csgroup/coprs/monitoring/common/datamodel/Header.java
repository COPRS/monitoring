package eu.csgroup.coprs.monitoring.common.datamodel;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", timezone = "UTC")
    private Instant timestamp;

    @NotNull
    private TraceLevel level;

    @NotNull
    private Mission mission;

    private Workflow workflow;

    Boolean debugMode;

    List<String> tagList;
}
