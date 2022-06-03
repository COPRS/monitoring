package eu.csgroup.coprs.monitoring.common.datamodel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.vladmihalcea.hibernate.type.json.JsonType;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@TypeDef(name = "json", typeClass = JsonType.class)
public class Trace {
    @Id
    @GeneratedValue
    @JsonIgnore
    private Long id;

    @OneToOne(cascade = {CascadeType.ALL})
    @JoinColumn( name="headerId" )
    private Header header;

    //private Message message;

    @OneToOne(cascade = {CascadeType.ALL})
    @JoinColumn( name="taskId" )
    private Task task;

    @Type(type = "json")
    @Column(columnDefinition = "json")
    private String custom;
    @Type(type = "json")
    @Column(columnDefinition = "json")
    private String kubernetes;


    public void setCustom(JsonNode custom) {
        this.custom = custom.toString();
    }

    /*@JsonRawValue
    public String getCustom() {
        // default raw value: null or "[]"
        return custom == null ? null : custom.toString();
    }*/

    public void setKubernetes(JsonNode kubernetes) {
        this.kubernetes = kubernetes.toString();
    }

    /*@JsonRawValue
    public String getKubernetes() {
        // default raw value: null or "[]"
        return kubernetes == null ? null : kubernetes.toString();
    }*/

    /*public static JsonDeserializer<Trace> deserializer() {
        return buildDeserializer(node -> {
                final var header = Header.readHeader(node.get(HEADER));
                final var message = Message.readMessage(node.get(MESSAGE));
                final var task = Optional.of(node.get(TASK))
                    .map(Task::readTask)
                    .orElse(null);
                final var custom = Optional.of(node.get(CUSTOM))
                    //.map(Custom::readCustom)
                    .map(JsonNode::asText)
                    .orElse(null);
                final var kubernetes =node.get(KUBERNETES).toString();

                return new Trace(
                    header,
                    message,
                    task,
                    custom,
                    kubernetes
                );
            }
        );
    }*/
}
