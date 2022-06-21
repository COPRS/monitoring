package eu.csgroup.coprs.monitoring.common.datamodel;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Trace {
    @NotNull
    @Valid
    private Header header;

    @NotNull
    @Valid
    private Message message;

    @NotNull
    @Valid
    private Task task;

    private String custom;

    @NotNull
    private String kubernetes;


    public void setCustom(JsonNode custom) {
        this.custom = custom.toString();
    }

    public void setKubernetes(JsonNode kubernetes) {
        this.kubernetes = kubernetes.toString();
    }
}
