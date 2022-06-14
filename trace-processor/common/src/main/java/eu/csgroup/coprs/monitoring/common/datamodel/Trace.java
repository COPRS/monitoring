package eu.csgroup.coprs.monitoring.common.datamodel;

import com.fasterxml.jackson.databind.JsonNode;
import com.vladmihalcea.hibernate.type.json.JsonType;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@TypeDef(name = "json", typeClass = JsonType.class)
public class Trace {
    /*@Id
    @GeneratedValue
    @JsonIgnore
    private Long id;*/

    @OneToOne(cascade = {CascadeType.ALL})
    @JoinColumn( name="headerId" )
    @NotNull
    @Valid
    private Header header;

    @NotNull
    @Valid
    private Message message;

    @OneToOne(cascade = {CascadeType.ALL})
    @JoinColumn( name="taskId" )
    @NotNull
    @Valid
    private Task task;

    @Type(type = "json")
    @Column(columnDefinition = "json")
    private String custom;

    @Type(type = "json")
    @Column(columnDefinition = "json")
    @NotNull
    private String kubernetes;


    public void setCustom(JsonNode custom) {
        this.custom = custom.toString();
    }

    public void setKubernetes(JsonNode kubernetes) {
        this.kubernetes = kubernetes.toString();
    }
}
