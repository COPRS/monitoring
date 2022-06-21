package eu.csgroup.coprs.monitoring.common.datamodel;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.JsonNode;
import eu.csgroup.coprs.monitoring.common.json.PropertyNames;
import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;


@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "event", visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = BeginTask.class, name = "begin"),
    @JsonSubTypes.Type(value = EndTask.class, name = "end") }
)
public class Task {
    @NotNull
    @Pattern(regexp = PropertyNames.UID_REGEX, message = "Task uid does not match UID pattern")
    private String uid;

    @NotNull
    @Size(max = 256, message = "Task name cannot exceed 256 characters")
    private String name;

    @NotNull
    private Event event;

    private double dataRateMebibytesSec;
    private double dataVolumeMebibytes;

    private Satellite satellite;

    @NotNull
    private String input;

    public void setInput(JsonNode input) {
        this.input = input.toString();
    }

    @JsonRawValue
    public String getInput() {
        // default raw value: null or "[]"
        return input == null ? null : input.toString();
    }
}
