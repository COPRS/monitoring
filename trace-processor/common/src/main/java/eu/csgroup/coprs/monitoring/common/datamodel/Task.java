package eu.csgroup.coprs.monitoring.common.datamodel;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.vladmihalcea.hibernate.type.basic.PostgreSQLEnumType;
import com.vladmihalcea.hibernate.type.json.JsonType;
import eu.csgroup.coprs.monitoring.common.json.PropertyNames;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;
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
@TypeDefs({
    @TypeDef(name = "pgsql_enum", typeClass = PostgreSQLEnumType.class),
    @TypeDef(name = "json", typeClass = JsonType.class)
})
public class Task {
    /*@Id
    @GeneratedValue
    @JsonIgnore
    private Long id;*/

    @NotNull
    @Pattern(regexp = PropertyNames.UID_REGEX, message = "Task uid does not match UID pattern")
    private String uid;

    @NotNull
    @Size(max = 256, message = "Task name cannot exceed 256 characters")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "event")
    @Type( type = "pgsql_enum" )
    @NotNull
    private Event event;

    private double dataRateMebibytesSec;
    private double dataVolumeMebibytes;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "satellite")
    @Type( type = "pgsql_enum" )
    private Satellite satellite;

    @Type(type = "json")
    @Column(columnDefinition = "json")
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
