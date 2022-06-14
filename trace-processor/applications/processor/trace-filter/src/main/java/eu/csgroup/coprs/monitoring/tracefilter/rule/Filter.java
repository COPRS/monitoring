package eu.csgroup.coprs.monitoring.tracefilter.rule;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.function.Predicate;

@Data
@Slf4j
public class Filter implements Predicate<JsonNode> {
    private String name;
    private List<Rule> rules;


    public void setRules (Map<String, String> rules) {
        this.rules = new Vector<>();
        rules.entrySet().stream().forEach(entry -> {
            this.rules.add(new Rule(entry.getKey(), entry.getValue()));
        });
    }

    @Override
    public boolean test(JsonNode jsonNode) {
        return rules.stream()
                .map(rule -> {
                    final var path = getJsonPathFromRule(rule);
                    final var jsonValue = jsonNode.at(path);
                    var match = false;
                    if (jsonValue != null) {
                        match = rule.test(jsonValue.asText());
                    }

                    if (!match) {
                        log.trace("Rule: %s does not match".formatted(rule));
                    }
                    return match;
                }).reduce(true, (last, next) -> last & next);
    }

    private String getJsonPathFromRule (Rule rule) {
        return "/" + rule.getKey().replaceAll("\\.", "/");
    }
}
