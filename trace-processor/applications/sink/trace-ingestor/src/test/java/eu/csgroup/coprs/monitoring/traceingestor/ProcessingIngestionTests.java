/*
 * Copyright 2023 CS Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.csgroup.coprs.monitoring.traceingestor;

import eu.csgroup.coprs.monitoring.common.bean.ReloadableBeanFactory;
import eu.csgroup.coprs.monitoring.common.datamodel.*;
import eu.csgroup.coprs.monitoring.common.datamodel.entities.*;
import eu.csgroup.coprs.monitoring.common.ingestor.EntityIngestor;
import eu.csgroup.coprs.monitoring.common.message.FilteredTrace;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@Import(TraceIngestorConfiguration.class)
@ContextConfiguration(initializers = TestInitializer.class)
@DataJpaTest
// Comment the two below annotation to test with non embedded database
@AutoConfigureEmbeddedDatabase
@ActiveProfiles("dev-embedded")
//@ActiveProfiles("dev-integration")
public class ProcessingIngestionTests {

    private static final TraceIngestorConfiguration conf = new TraceIngestorConfiguration();

    @Autowired
    private ReloadableBeanFactory factory;

    @Autowired
    private EntityIngestor entityIngestor;


    @After
    public void setUpAfterTest() {
        entityIngestor.deleteAll();
    }

    @Test
    public void testNominal() {
        // Given
        final var sink = conf.traceIngestor(factory, entityIngestor);
        final var processingRef = getProcessingRef("processing_all");

        // When
        sink.accept(toMessage(processingRef));

        // Then
        assertThat(entityIngestor.findAll(Dsib.class))
                .hasSize(2);
        assertThat(entityIngestor.findAll(Chunk.class))
                .hasSize(6);
        assertThat(entityIngestor.findAll(AuxData.class))
                .hasSize(3);
        assertThat(entityIngestor.findAll(InputListExternal.class))
                .hasSize(9);
        assertThat(entityIngestor.findAll(Processing.class))
                .hasSize(1);
        assertThat(entityIngestor.findAll(Product.class))
                .hasSize(6);
        assertThat(entityIngestor.findAll(InputListInternal.class))
                .hasSize(2);
        assertThat(entityIngestor.findAll(OutputList.class))
                .hasSize(4);
        assertThat(entityIngestor.findAll(MissingProducts.class))
                .isEmpty();
    }

    @Test
    public void testWithMissingOutput() {
        // Given
        final var sink = conf.traceIngestor(factory, entityIngestor);
        final var processingRef = getProcessingRefWithMissingProducts("processing_all");

        // When
        sink.accept(toMessage(processingRef));

        // Then
        final var processing = entityIngestor.findAll(Processing.class).get(0);
        assertThat(entityIngestor.findAll(MissingProducts.class))
                .hasSize(3)
                .extracting(MissingProducts::getProcessing)
                .extracting(Processing::getId)
                .allMatch(processingFailedId -> processingFailedId.equals(processing.getId()));
    }

    @Test
    public void testExistingEntity() {
        // Given
        final var sink = conf.traceIngestor(factory, entityIngestor);
        final var processingRef = getProcessingRef("processing_all");

        final var dsib = new Dsib();
        dsib.setFilename("DCS_05_S2B_20210927072424023820_ch1_DSIB.xml");

        entityIngestor.process((ei) -> List.of(
                dsib
        ));

        // When
        sink.accept(toMessage(processingRef));

        // Then
        assertThat(entityIngestor.findAll(Chunk.class))
                .isNotEmpty();
    }

    @Test
    public void testInputOnly() {
        // Given
        final var sink = conf.traceIngestor(factory, entityIngestor);
        final var processingRef = getProcessingRef("processing_input");

        // When
        sink.accept(toMessage(processingRef));

        // Then
        assertThat(entityIngestor.findAll(Product.class))
                .isEmpty();
    }

    @Test
    public void testOutputOnly() {
        // Given
        final var sink = conf.traceIngestor(factory, entityIngestor);
        final var processingRef = getProcessingRef("processing_output");

        // When
        sink.accept(toMessage(processingRef));

        // Then
        assertThat(entityIngestor.findAll(ExternalInput.class))
                .isEmpty();
    }

    @Test
    public void testGenerates2L1Duplicate() {
        FilteredTrace filteredTrace = getProcessingRefWithKube("duplicate_processing", "ew-l1s");
        TraceIngestorSink sink = conf.traceIngestor(factory, entityIngestor);


        //when
        sink.accept(toMessage(filteredTrace));
        //trigger duplication
        sink.accept(toMessage(filteredTrace));

        //Then
        List<Processing> processings = entityIngestor.findAll(Processing.class);
        assertThat(processings)
                .hasSize(2)
                .anyMatch(Processing::isDuplicate);

    }

    @Test
    public void testKubernetesRecognized() {
        FilteredTrace traceLog = getProcessingRefWithKube("duplicate_processing", "2-l1-1574-part1-ew-l1c-v1");
        TraceIngestorSink sink = conf.traceIngestor(factory, entityIngestor);


        //when
        sink.accept(toMessage(traceLog));
        //trigger duplication
        sink.accept(toMessage(traceLog));

        //Then
        List<Processing> processings = entityIngestor.findAll(Processing.class);
        assertThat(processings)
                .hasSize(2)
                .noneMatch(Processing::isDuplicate);

    }

    // -- Helper -- //
    private FilteredTrace getProcessingRefWithMissingProducts(String filterName) {
        final var ref = getProcessingRef(filterName);

        final var missingOutput1 = new HashMap<String, Object>();
        missingOutput1.put("estimated_count_integer", 8);
        missingOutput1.put("end_to_end_product_boolean", true);
        missingOutput1.put("product_metadata_custom_object", List.of(Map.of("pmco1", "value1"), Map.of("pmco2", "value2")));

        final var missingOutput2 = new HashMap<String, Object>();
        missingOutput2.put("estimated_count_integer", 2);
        missingOutput2.put("end_to_end_product_boolean", false);
        missingOutput2.put("product_metadata_custom_object", List.of(Map.of("pmco3", "value3")));

        ((EndTask) (ref.getLog().getTrace().getTask())).setMissingOutput(List.of(missingOutput1, missingOutput2));

        return ref;
    }

    private FilteredTrace getProcessingRef(String filterName) {
        final var header = new Header();
        header.setType(TraceType.REPORT);
        header.setMission("S2");
        header.setLevel(Level.INFO);
        header.setWorkflow(Workflow.NOMINAL);
        header.setTimestamp(Instant.parse("2022-10-05T14:13:30.00Z"));

        final var task = new EndTask();
        //task.setSatellite("S2B");

        final var output = new HashMap<String, Object>();
        output.put("filename_strings", List.of(
                "GS2B_20170322T000000_013601_N02.01",
                "GS2B_20170322T000000_013601_N02.02.zip",
                "GS2B_20170322T000000_013601_N02.03.zip",
                "GS2B_20170322T000000_013601_N02.04")
        );
        task.setOutput(output);

        final var input = new HashMap<String, Object>();
        input.put("filename_strings", List.of(
                "DCS_05_S2B_20210927072424023813_ch1_DSDB_00001.raw",
                "DCS_05_S2B_20210927072424023813_ch1_DSDB_00002.raw",
                "DCS_05_S2B_20210927072424023813_ch1_DSDB_00003.raw",
                "DCS_05_S2B_20210927072424023813_ch2_DSDB_00001.raw",
                "DCS_05_S2B_20210927072424023813_ch2_DSDB_00002.raw",
                "DCS_05_S2B_20210927072424023813_ch2_DSDB_00003.raw",
                "S1A_OPER_AMH_ERRMAT_W.XML",
                "S2A_OPER_AUX_TEST_TE",
                "S3A_OL_0_TESTAX_12345678T123456_12345678T123456_12345678T123456___________________123_12345678.SEN3",
                "GS2B_20170322T000000_013601_N02.05",
                "GS2B_20170322T000000_013601_N02.06.zip")
        );
        task.setInput(input);

        task.setDurationInSeconds(60.0);

        task.setMissingOutput(List.of());

        final var trace = new Trace();
        trace.setHeader(header);
        trace.setTask(task);

        final var custom = new HashMap<String, Object>();
        custom.put("key1", "value1");
        custom.put("key2", "value2");
        custom.put("key3", "value3");
        trace.setCustom(custom);

        final var traceLog = new TraceLog();
        traceLog.setTrace(trace);
        return new FilteredTrace(filterName, traceLog);
    }

    private FilteredTrace getProcessingRefWithKube(String filterName, String apllicationName) {
        final var header = new Header();
        header.setType(TraceType.REPORT);
        header.setMission("S2");
        header.setLevel(Level.INFO);
        header.setWorkflow(Workflow.NOMINAL);
        header.setTimestamp(Instant.parse("2022-10-05T14:13:30.00Z"));
        header.setRsChainName("s2-l1");
        header.setRsChainVersion("1.5.0-dev");

        final var task = new EndTask();
        //task.setSatellite("S2B");

        final var output = new HashMap<String, Object>();
        output.put("filename_strings", List.of(
                "GS2B_20170322T000000_013601_N02.01",
                "GS2B_20170322T000000_013601_N02.02.zip",
                "GS2B_20170322T000000_013601_N02.03.zip",
                "GS2B_20170322T000000_013601_N02.04")
        );
        task.setOutput(output);

        final var input = new HashMap<String, Object>();
        input.put("filename_strings", List.of(
                "DCS_05_S2B_20210927072424023813_ch1_DSDB_00001.raw",
                "DCS_05_S2B_20210927072424023813_ch1_DSDB_00002.raw",
                "DCS_05_S2B_20210927072424023813_ch1_DSDB_00003.raw",
                "DCS_05_S2B_20210927072424023813_ch2_DSDB_00001.raw",
                "DCS_05_S2B_20210927072424023813_ch2_DSDB_00002.raw",
                "DCS_05_S2B_20210927072424023813_ch2_DSDB_00003.raw",
                "S1A_OPER_AMH_ERRMAT_W.XML",
                "S2A_OPER_AUX_TEST_TE",
                "S3A_OL_0_TESTAX_12345678T123456_12345678T123456_12345678T123456___________________123_12345678.SEN3",
                "GS2B_20170322T000000_013601_N02.05",
                "GS2B_20170322T000000_013601_N02.06.zip")
        );

        final Map<String, Object> kube = new HashMap<>();
        kube.put(
                "labels", Map.of(
                        "spring-application-name", apllicationName,
                        "spring-app-id", "s2-l1-1574-part1-ew-l1s-v1"
                ));
        task.setInput(input);

        task.setDurationInSeconds(60.0);

        task.setMissingOutput(List.of());

        final var trace = new Trace();
        trace.setHeader(header);
        trace.setTask(task);

        final var custom = new HashMap<String, Object>();
        custom.put("key1", "value1");
        custom.put("key2", "value2");
        custom.put("key3", "value3");
        trace.setCustom(custom);

        final var traceLog = new TraceLog();
        traceLog.setTrace(trace);
        traceLog.setKubernetes(kube);

        return new FilteredTrace(filterName, traceLog);
    }

    private Message<FilteredTrace> toMessage(FilteredTrace ref) {
        return new GenericMessage<>(ref);
    }
}