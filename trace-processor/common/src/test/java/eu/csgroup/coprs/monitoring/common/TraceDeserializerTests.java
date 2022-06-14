package eu.csgroup.coprs.monitoring.common;

import com.fasterxml.jackson.databind.json.JsonMapper;
import eu.csgroup.coprs.monitoring.common.datamodel.*;
import lombok.NonNull;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.slf4j.Logger;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.slf4j.LoggerFactory.getLogger;


public class TraceDeserializerTests {

    private static final Logger LOGGER = getLogger(TraceDeserializerTests.class);


    @Test
    public void testNominal() throws IOException {
        // Given
        final var mapper = JsonMapper.builder()
                .findAndAddModules()
                .build();
        final var expected = getExpected(mapper);
        System.out.println("Expected: " + expected);

        // When
        final var trace = loadTrace("trace.json");
        System.out.println("Got: " + trace);

        // Then
        assertThat(trace).isEqualTo(expected);

        String pattern="^.*$";
        String regex="^[0-9]{2}$";
        System.out.println(regex.matches(pattern));
    }

    @Test
    public void testMissingRequired() throws IOException {
        // Given

        // When

        // Then
        assertThatThrownBy(() -> loadTrace("trace-MissingRequired.json")).isNotNull();
    }

    @Test
    public void testInvalidUid() throws IOException {
        // Given
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        final var trace = loadTrace("trace-InvalidUid.json");

        // When
        Set<ConstraintViolation<Trace>> violations = validator.validate(trace);

        // Then
        assertThat(violations).isNotEmpty();
    }

    @Test
    public void testInvalidTimestamp() throws IOException {
        // Given

        // When

        // Then
        assertThatThrownBy(() -> loadTrace("trace-InvalidTimestamp.json")).isNotNull();
    }

    @Test
    public void testInvalidContent() throws IOException {
        // Given
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        final var trace = loadTrace("trace-InvalidContent.json");

        // When
        Set<ConstraintViolation<Trace>> violations = validator.validate(trace);

        // Then
        assertThat(violations).isNotEmpty();
    }


    // -- Helper -- //

    protected Trace loadTrace(String classpathResource, JsonMapper mapper) throws IOException{
        final var content = getContent(classpathResource);

        return mapper.readValue(content, Trace.class);
    }

    protected Trace loadTrace(String classpathResource) throws IOException{
        final var mapper = JsonMapper.builder()
                .findAndAddModules()
                .build();

        return loadTrace(classpathResource, mapper);
    }

    protected String getContent(String classpathResource) {
        try {
            return IOUtils.resourceToString(classpathResource, StandardCharsets.UTF_8, TraceDeserializerTests.class.getClassLoader());
        } catch (IOException e) {
            throw new RuntimeException("Cannot retrieve content of resource %s".formatted(classpathResource), e);
        }
    }

    protected Trace getExpected (JsonMapper mapper) throws IOException{
        final var header = new Header();
        header.setType(TraceType.REPORT);
        header.setTimestamp(Instant.parse("2021-08-30T15:02:24.125000Z"));
        header.setLevel(TraceLevel.INFO);
        header.setMission(Mission.S3);
        header.setWorkflow(Workflow.NOMINAL);

        final var message = new Message();
        message.setContent("Start compression processing");
        final var task = new BeginTask();
        task.setUid("4cb9fa49-2c0a-4363-82c3-ea9ab223c53a");
        task.setName("CompressionProcessing");
        task.setEvent(Event.begin);
        task.setInput(mapper.readTree("{}"));
        task.setFollowsFromTask("a66d3ac2-2483-4891-8151-1bc77e4296e8");

        final var kubernetes = mapper.readTree(
                "{\n" +
                "    \"pod_name\": \"s1pro-compression-worker-0\",\n" +
                "    \"namespace_name\": \"processing\",\n" +
                "    \"pod_id\": \"d9e908c8-459c-410f-b538-18e3c442c877\",\n" +
                "    \"labels\": {\n" +
                "      \"app\": \"s1pro-compression-worker\",\n" +
                "      \"controller-revision-hash\": \"s1pro-compression-worker-649db58b55\",\n" +
                "      \"statefulset_kubernetes_io/pod-name\": \"s1pro-compression-worker-0\",\n" +
                "      \"type\": \"processing\"\n" +
                "    },\n" +
                "    \"annotations\": {\n" +
                "      \"cni_projectcalico_org/podIP\": \"10.244.56.3/32\",\n" +
                "      \"cni_projectcalico_org/podIPs\": \"10.244.56.3/32\",\n" +
                "      \"kubernetes_io/psp\": \"privileged\"\n" +
                "    },\n" +
                "    \"host\": \"ops-c1-wrapper-zip-0099\",\n" +
                "    \"container_name\": \"s1pro-compression-worker\",\n" +
                "    \"docker_id\": \"2dee31be4d19ae5d0e5347b9bf29327ea5b0385530bbfd4f88515536f60a0804\"\n" +
                "  }");

        final var trace = new Trace();
        trace.setHeader(header);
        trace.setMessage(message);
        trace.setTask(task);
        trace.setKubernetes(kubernetes);

        return trace;
    }
}
