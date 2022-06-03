package eu.csgroup.coprs.monitoring.traceingestor;

import eu.csgroup.coprs.monitoring.common.datamodel.Trace;
import eu.csgroup.coprs.monitoring.common.config.TraceConfiguration;
import org.slf4j.Logger;
import org.springframework.messaging.Message;

import java.util.function.Consumer;

import static org.slf4j.LoggerFactory.getLogger;

public class TraceIngestorSink implements Consumer<Message<Trace>> {

    private static final Logger LOGGER = getLogger(TraceIngestorSink.class);


    private final TraceConfiguration traceService;

    public TraceIngestorSink(TraceConfiguration traceService) {
        this.traceService = traceService;
    }

    @Override
    public void accept(Message<Trace> jsonContent) {
        ingestLog(jsonContent.getPayload());
    }


    protected final void ingestLog(Trace trace) {
        System.out.println(trace.toString());
        traceService.add(trace);
    }
}
