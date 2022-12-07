package eu.csgroup.coprs.monitoring.common.datamodel.entities;

import lombok.*;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;

@Data
@EqualsAndHashCode()
@ToString()
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class OutputListId implements Serializable {
    @Transient
    @Serial
    private static final long serialVersionUID = -6147645249469845263L;


    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "processing_id")
    private Processing processing;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "product_id")
    private Product product;
}