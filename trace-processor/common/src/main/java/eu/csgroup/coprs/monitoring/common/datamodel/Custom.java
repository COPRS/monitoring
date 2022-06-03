package eu.csgroup.coprs.monitoring.common.datamodel;

import com.fasterxml.jackson.databind.JsonNode;


public record Custom(
    String content
) {
    public static Custom readCustom(JsonNode node) {
        final var content = node.get("content").asText();

        return new Custom(content);
    }

}
