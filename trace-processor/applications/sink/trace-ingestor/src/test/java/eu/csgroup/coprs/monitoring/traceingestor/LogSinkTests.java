package eu.csgroup.coprs.monitoring.traceingestor;

import eu.csgroup.coprs.monitoring.common.datamodel.Header;
import eu.csgroup.coprs.monitoring.common.datamodel.Trace;
import eu.csgroup.coprs.monitoring.common.datamodel.TraceType;
import eu.csgroup.coprs.monitoring.common.config.TraceConfiguration;
import org.junit.*;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//@SpringBootTest
@ActiveProfiles("test")
//@ContextConfiguration(initializers = {LogSinkTests.Initializer.class})
public class LogSinkTests {
    /*@Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Before
    public void initIndex() {
        clientManager.monoWithCatalogClient(catalog, client ->
            client.createIndex(CATALOG_INDEX)
        ).block();
    }

    @After
    public void clearIndex() {
        clientManager.monoWithCatalogClient(catalog, client ->
            client.clearIndex(CATALOG_INDEX)
        ).block();
    }*/
    /**static class Initializer
        implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                "spring.datasource.driverClassName=org.h2.Driver",
                "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect"
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }*/


    @Autowired
    private TraceIngestorSink sink;

    @Autowired
    private TraceConfiguration traceService;

    @Test
    public void testDeserializer() {
        // Given
        /*final var mapper = Mappers.testMapper()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        System.out.println("1 " + mapper.isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
        System.out.println("1 " + mapper.isEnabled(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES));*/
        //final var logSink = config.logSink(mapper, traceService);
        final var ref = getRef();

        sink.accept(toMessage(ref));

        var res = traceService.list();
        assertThat(res).hasSize(1).isEqualTo(ref);
    }

    private Trace getRef () {
        final var header = new Header();
        header.setType(TraceType.REPORT);
        final var trace = new Trace();
        trace.setHeader(header);
        //trace.setCustom();
        //trace.setKubernetes();

        return trace;
    }

    private Message<Trace> toMessage(Trace ref) {
        return new GenericMessage<>(ref);
    }
}
