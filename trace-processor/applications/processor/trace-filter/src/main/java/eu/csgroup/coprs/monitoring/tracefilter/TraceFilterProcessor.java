package eu.csgroup.coprs.monitoring.tracefilter;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import eu.csgroup.coprs.monitoring.common.bean.ReloadableBeanFactory;
import eu.csgroup.coprs.monitoring.common.message.FilteredTrace;
import eu.csgroup.coprs.monitoring.common.datamodel.Trace;
import eu.csgroup.coprs.monitoring.tracefilter.json.JsonValidationException;
import eu.csgroup.coprs.monitoring.tracefilter.json.JsonValidator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import eu.csgroup.coprs.monitoring.tracefilter.rule.FilterGroup;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TraceFilterProcessor
    implements Function<Message<String>, List<Message<FilteredTrace>>> {

    private final JsonValidator jsonValidator;

    private final ReloadableBeanFactory factory;

    private String lastProcessedRawTrace;

    public TraceFilterProcessor(JsonValidator jsonValidator, ReloadableBeanFactory factory) {
        this.jsonValidator = jsonValidator;
        this.factory= factory;
    }

    public List<Message<FilteredTrace>> apply(Message<String> json) {
        String traceUid = null;
        try {
            if (lastProcessedRawTrace != null && lastProcessedRawTrace.equals(json.getPayload())) {
                log.warn("Retry last failed trace");
            } else {
                log.trace("Handle new trace");
            }
            final var trace = jsonValidator.readAndValidate(json.getPayload(), Trace.class);
            traceUid = trace.getTask().getUid();
            var traceLog = new StringBuilder("Trace (").append(traceUid)
                .append(") handled; ");

            JsonNode jsonNode = jsonValidator.getMapper().readTree(json.getPayload());

            var ruleMatch = factory.getBean(FilterGroup.class).apply(jsonNode);

            ruleMatch.ifPresentOrElse(filterName -> traceLog.append("filter '").append(filterName).append("' applied"),
                () -> traceLog.append("no filter applied")
            );
            log.debug(traceLog.toString());

            // Reset cached error trace
            lastProcessedRawTrace = null;

            // Create message
            return ruleMatch.map(filter -> new FilteredTrace(filter.getName(), trace))
                .map(ft -> MessageBuilder.withPayload(ft).build())
                .map(Collections::singletonList)
                .orElseGet(Collections::emptyList);
        } catch (JsonProcessingException | JsonValidationException e) {
            log.error("Wrong trace format (%s)".formatted(json.getPayload()), e);
            return Collections.emptyList();
        } catch (Exception e) {
            lastProcessedRawTrace = json.getPayload();
            final var errorMessage = "Error occurred handling trace \n%s: ".formatted(json.getPayload());
            throw new RuntimeException(errorMessage, e);
        }
    }
}
