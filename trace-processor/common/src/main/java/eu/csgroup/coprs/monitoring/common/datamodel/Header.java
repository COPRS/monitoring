package eu.csgroup.coprs.monitoring.common.datamodel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vladmihalcea.hibernate.type.array.ListArrayType;
import com.vladmihalcea.hibernate.type.basic.PostgreSQLEnumType;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@TypeDefs({
    @TypeDef(name = "pgsql_enum", typeClass = PostgreSQLEnumType.class),
    @TypeDef(name = "list-array", typeClass = ListArrayType.class)
})

public class Header {
    @Id
    @GeneratedValue
    @JsonIgnore
    private Long id;
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "trace_type")
    @Type( type = "pgsql_enum" )
    private TraceType type;
    /**private Instant timestamp;
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "trace_level")
    @Type( type = "pgsql_enum" )
    private TraceLevel level;
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "mission")
    @Type( type = "pgsql_enum" )
    private Mission mission;
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "workflow")
    @Type( type = "pgsql_enum" )
    private Workflow workflow;
    //@JsonProperty(DEBUG_MODE)
    private Boolean debugMode;
    //@JsonProperty(TAG_LIST)
    @Type( type = "list-array" )
    @Column(columnDefinition = "text[]")
    private List<String> tagList;*/

    /**public static Header readHeader(JsonNode node) {
        final var type = TraceType.valueOf(node.get(PropertyNames.TYPE).asText());
        final var timestamp = Instant.parse(node.get(PropertyNames.TIMESTAMP).asText());
        final var level = TraceLevel.valueOf(node.get(PropertyNames.LEVEL).asText());
        final var mission = Mission.valueOf(node.get(PropertyNames.MISSION).asText());
        final var workflow = Optional.of(node.get(PropertyNames.WORKFLOW))
            .map(JsonNode::asText)
            .map(Workflow::valueOf)
            .orElse(null);
        final var debugMode = Optional.of(node.get(PropertyNames.DEBUG_MODE))
            .map(JsonNode::asBoolean)
            .orElse(null);
        final var tagList = Optional.of(node.get(PropertyNames.TAG_LIST))
            .map(tag -> StreamSupport.stream(
                    Spliterators.spliteratorUnknownSize(tag.elements(), Spliterator.ORDERED), false
                ).map(child -> child.asText())
                .collect(Collectors.toList())
            ).orElse(null);


        return new Header(
            type,
            timestamp,
            level,
            mission,
            workflow,
            debugMode,
            tagList
        );
    }*/
}
