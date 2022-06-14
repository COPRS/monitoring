package eu.csgroup.coprs.monitoring.common.datamodel;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class EndTask extends Task {
    @NotNull
    private Status status;

    @NotNull
    private Integer errorCode;

    //TODO Set as Duration and not double
    @NotNull
    private Double durationInSeconds;

    @NotNull
    private Object output;

    @NotNull
    private Object quality;


    public void setOutput(JsonNode output) {
        this.output = output;
    }

    @JsonRawValue
    @JsonFormat
    public String getOutput() {
        // default raw value: null or "[]"
        return output == null ? null : output.toString();
    }

    public void setQuality(JsonNode quality) {
        this.quality = quality;
    }

    @JsonRawValue
    public String getQuality() {
        // default raw value: null or "[]"
        return quality == null ? null : quality.toString();
    }
}
