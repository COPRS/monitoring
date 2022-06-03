package eu.csgroup.coprs.monitoring.common.datamodel;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EndTask extends Task {
    private Status status;

    private Integer errorCode;

    //TODO Set as Duration and not double
    private Double durationInSeconds;

    private Object output;

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

    public void setquality(JsonNode quality) {
        this.quality = quality;
    }

    @JsonRawValue
    public String getQuality() {
        // default raw value: null or "[]"
        return quality == null ? null : quality.toString();
    }
}
