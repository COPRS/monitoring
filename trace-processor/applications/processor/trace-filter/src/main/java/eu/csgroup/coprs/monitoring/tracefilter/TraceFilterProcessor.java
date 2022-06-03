package eu.csgroup.coprs.monitoring.tracefilter;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import eu.csgroup.coprs.monitoring.common.message.FilteredTrace;
import eu.csgroup.coprs.monitoring.common.datamodel.Trace;
import eu.csgroup.coprs.monitoring.tracefilter.rule.FilteringRule;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TraceFilterProcessor
    implements Function<Message<String>, List<Message<FilteredTrace>>> {

    private final ObjectMapper mapper;

    private final FilteringRule filters;

    public TraceFilterProcessor(ObjectMapper jsonMapper, FilteringRule filters) {
        this.mapper = jsonMapper;
        this.filters = filters;
    }

    public List<Message<FilteredTrace>> apply(Message<String> json) {
        try {
            log.debug("Handle new trace");
            final var trace = mapper.readValue(json.getPayload(), Trace.class);
            var traceLog = new StringBuilder("Trace: ").append(trace.getTask().getUid())
                .append(" handled; ");

            JsonNode jsonNode = mapper.readTree(json.getPayload());

            var ruleMatch = filters.getRules()
                .stream()
                .filter(rule -> {
                    log.trace("Check rule " + rule.getName());
                    return rule.getConditions().entrySet().stream().map(condition -> {
                        var jsonValue = jsonNode.at("/" + condition.getKey().replaceAll("\\.", "/"));
                        var match = false;
                        if (jsonValue != null) {
                            var rawValue = jsonValue.asText();
                            match = rawValue.equals(condition.getValue());
                            log.trace("Path: " + "/" + condition.getKey().replaceAll("\\.", "/") + " Value: " + jsonValue.toString());
                        }
                        log.trace("Result for condition: " + condition.getKey() + " is: " + match);
                        return match;
                    }).reduce(true, (last, next) -> last & next );
                }).findFirst();

            ruleMatch.ifPresentOrElse(rule -> traceLog.append("matching rule ").append(rule.getName()),
                () -> traceLog.append("no matching rule")
            );

            log.debug(traceLog.toString());

            return ruleMatch.map(rule -> new FilteredTrace(rule.getName(), trace))
                .map(ft -> MessageBuilder.withPayload(ft).build())
                .map(Collections::singletonList)
                .orElseGet(Collections::emptyList);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error occured while mapping trace", e);
        }
    }
}
