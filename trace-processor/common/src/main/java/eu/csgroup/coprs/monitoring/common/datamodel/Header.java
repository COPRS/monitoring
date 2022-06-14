package eu.csgroup.coprs.monitoring.common.datamodel;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vladmihalcea.hibernate.type.array.ListArrayType;
import com.vladmihalcea.hibernate.type.basic.PostgreSQLEnumType;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@TypeDefs({
    @TypeDef(name = "pgsql_enum", typeClass = PostgreSQLEnumType.class),
    @TypeDef(name = "list-array", typeClass = ListArrayType.class)
})
public class Header {
    /*@Id
    @GeneratedValue
    @JsonIgnore
    private Long id;*/

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "trace_type")
    @Type( type = "pgsql_enum" )
    @NotNull
    private TraceType type;

    @NotNull
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", timezone = "UTC")
    private Instant timestamp;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "trace_level")
    @Type( type = "pgsql_enum" )
    @NotNull
    private TraceLevel level;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "mission")
    @Type( type = "pgsql_enum" )
    @NotNull
    private Mission mission;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "workflow")
    @Type( type = "pgsql_enum" )
    private Workflow workflow;

    Boolean debugMode;

    @Type( type = "list-array" )
    @Column(columnDefinition = "text[]")
    List<String> tagList;
}
