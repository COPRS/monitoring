package eu.csgroup.coprs.monitoring.common.datamodel;

import eu.csgroup.coprs.monitoring.common.json.PropertyNames;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

public record Message (
    @JsonProperty(PropertyNames.CONTENT)
    String content
) {
    public static Message readMessage(JsonNode node) {
        final var content = node.get(PropertyNames.CONTENT).asText();

        return new Message(content);
    }

}
