package eu.csgroup.coprs.monitoring.common.datamodel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.databind.JsonNode;
import com.vladmihalcea.hibernate.type.basic.PostgreSQLEnumType;
import com.vladmihalcea.hibernate.type.json.JsonType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonSubTypes({
    @JsonSubTypes.Type(value = BeginTask.class, name = "BeginTask"),
    @JsonSubTypes.Type(value = EndTask.class, name = "EndTask") }
)
@TypeDefs({
    @TypeDef(name = "pgsql_enum", typeClass = PostgreSQLEnumType.class),
    @TypeDef(name = "json", typeClass = JsonType.class)
})
public class Task {
    @Id
    @GeneratedValue
    @JsonIgnore
    private Long id;

    private String uid;
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "event")
    @Type( type = "pgsql_enum" )
    private Event event;

    private double dataRateMebibytesSec;
    private double dataVolumeMebibytes;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "satellite")
    @Type( type = "pgsql_enum" )
    private Satellite satellite;

    @Type(type = "json")
    @Column(columnDefinition = "json")
    private Object input;


    public void setInput(JsonNode input) {
        this.input = input;
    }

    @JsonRawValue
    public String getInput() {
        // default raw value: null or "[]"
        return input == null ? null : input.toString();
    }

    /*public static Task readTask(JsonNode node) {
        new Task();
        final var uid = node.get(PropertyNames.UID).asText();
        final var name = node.get(PropertyNames.NAME).asText();
        final var event = Event.valueOf(
            node.get(PropertyNames.EVENT)
                .asText()
        );
        final var dataRateMebibytesSec = node.get(PropertyNames.DATA_RATE_MEBIBYTES_SEC)
            .asDouble();
        final var dataVolumeMebibytes = node.get(PropertyNames.DATA_VOLUME_MEBIBYTES)
            .asDouble();
        final var satellite = Satellite.valueOf(
            node.get(PropertyNames.SATELLITE).asText()
        );

        //final var input = formatArray(node);
        final var input = Optional.of(
                node.get(PropertyNames.INPUT)
            ).map(JsonNode::toString)
            .orElse(null);

        final var childOfTask = Optional.of(
                node.get(PropertyNames.CHILD_OF_TASK)
            ).map(JsonNode::asText)
            .orElse(null);

        final var followsFromTask = Optional.of(
                node.get(PropertyNames.FOLLOWS_FROM_TASK)
            ).map(JsonNode::toString)
            .orElse(null);

        final var status = Status.valueOf(
            node.get(PropertyNames.STATUS)
                .asText()
        );

        final var error_code = node.get(PropertyNames.ERROR_CODE).asInt();

        final var durationInSeconds = node.get(PropertyNames.DURATION_IN_SECONDS).asDouble();

        final var output = node.get(PropertyNames.OUTPUT).toString();

        final var quality = node.get(PropertyNames.QUALITY).toString();

        return new Task(
            uid,
            name,
            event,
            dataRateMebibytesSec,
            dataVolumeMebibytes,
            satellite,
            input
        );
    }*/

    /*private static Attribute formatValue(java.util.Map.Entry<String, JsonNode> entry) {
        final var suffix = Option.some(entry.getKey().lastIndexOf('_'))
            .filter(index -> index != 0)
            .map(index -> entry.getKey().substring(index))
            .getOrElse("");

        return switch (suffix) {
            case "_long" -> {
                yield new LongAttribute(entry.getKey(), entry.getValue().asLong());
            }
            case "_longs" -> {
                entry.getValue().elements()
            }
            case "_integer" -> {
                yield new IntegerAttribute(entry.getKey(), entry.getValue().asInt());
            }
            case "_short" -> {
                yield new IntegerAttribute(entry.getKey(), entry.getValue().asInt());
            }
            case "_byte" -> {
                yield new ByteAttribute(entry.getKey(), Byte.valueOf(entry.getValue().asText()));
            }
            case "_double" -> {
                yield new DoubleAttribute(entry.getKey(), entry.getValue().asDouble());
            }
            case "_float" -> {
                yield  new FloatAttribute(entry.getKey(), Float.valueOf(entry.getValue().asText()));
            }
            case "_boolean" -> {
                yield new BooleanAttribute(entry.getKey(), entry.getValue().asBoolean());
            }
            case "_date" -> {
                yield new DateAttribute(entry.getKey(), Instant.parse(entry.getValue().asText());
            }
            case "_object" -> {
                yield new ArrayAttribute(entry.getKey(), formatArray(entry.getValue()));
            }
            default -> {
                yield new StringAttribute(entry.getKey(), entry.getValue().asText());
            }
        };
    }

    private static Attribute formatValue(Class<? extends Attribute> type, JsonNode node) {
        type.
        var res = Attribute.class;
    }

    private static Seq<String> formatArray(Iterable<JsonNode> array) {
        return Vector.ofAll(array)
            .map(JsonNode::asText);
    }

    private static Seq<Attribute> formatArray(JsonNode arrayNode) {
        return Vector.ofAll(arrayNode::fields)
            .map(Task::formatValue);
    }*/
}
