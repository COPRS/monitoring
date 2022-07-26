package eu.csgroup.coprs.monitoring.common.datamodel.entities;

import eu.csgroup.coprs.monitoring.common.bean.AutoMergeableMap;
import lombok.Data;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import java.time.Instant;

@Data
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class ExternalInput extends DefaultEntity {
    @Id
    @SequenceGenerator(sequenceName="external_input_id_seq", name = "external_input_id_seq", allocationSize=1)
    @GeneratedValue(generator = "external_input_id_seq", strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false, unique = true)
    private String filename;

    private String mission;

    private Instant pickupPointSeenDate;

    private Instant pickupPointAvailableDate;

    private Instant ingestionDate;

    private Instant catalogStorageDate;

    @Type( type = "json" )
    @Column(columnDefinition = "json")
    private AutoMergeableMap custom;
}
