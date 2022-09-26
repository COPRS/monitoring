package eu.csgroup.coprs.monitoring.common.spring;

import eu.csgroup.coprs.monitoring.common.datamodel.entities.*;
import eu.csgroup.coprs.monitoring.common.ingestor.EntityIngestor;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@Import(EntityIngestor.class)
@DataJpaTest
@AutoConfigureEmbeddedDatabase
@ActiveProfiles("dev-embedded")
public class EntityIngestorTest {

    @Autowired
    private EntityIngestor entityIngestor;

    @Test
    public void testSave() {
        // Given
        final var dsib = new Dsib();
        dsib.setFilename("test");

        // When
        final var res = entityIngestor.saveAll(List.of(dsib));

        // Then
        assertThat(res).hasSize(1).allMatch(entity -> ((ExternalInput)entity).getId() != null);
    }


    @Test
    public void testFindAll() {
        // Given
        final var dsib = new Dsib();
        dsib.setFilename("test");
        entityIngestor.saveAll(List.of(dsib));

        // When
        final var res = entityIngestor.findAll(Dsib.class);


        // Then
        assertThat(res).hasSize(1).allMatch(entity -> entity.getId() != null);
    }

    @Test
    public void testOrderEntity () {
        // Given
        final var dsib = new Dsib();
        dsib.setFilename("dsib");
        final var chunk = new Chunk();
        chunk.setFilename("chunk");
        final var auxData = new AuxData();
        auxData.setFilename("auxData");
        final var processing = new Processing();
        final var inputListExternal = new InputListExternal();
        inputListExternal.getId().setProcessing(processing);
        inputListExternal.getId().setExternalInput(dsib);

        final var product = new Product();
        final var inputListInternal = new InputListInternal();
        inputListInternal.getId().setProcessing(processing);
        inputListInternal.getId().setProduct(product);

        final var outputList = new OutputList();
        outputList.getId().setProcessing(processing);
        outputList.getId().setProduct(product);

        final var missingProduct = new MissingProducts();
        missingProduct.setProcessing(processing);

        // When
        final var list = new LinkedList<DefaultEntity>();
        list.add(dsib);
        list.add(inputListExternal);
        list.add(inputListInternal);
        list.add(processing);
        list.add(chunk);
        list.add(outputList);
        list.add(missingProduct);
        list.add(product);
        list.add(auxData);
        final var res = entityIngestor.saveAll(list);

        // Then
        assertThat(res).isNotNull().isNotEmpty();
    }
}
