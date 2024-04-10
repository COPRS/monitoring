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

package eu.csgroup.coprs.monitoring.common.datamodel.entities;

import com.vladmihalcea.hibernate.type.basic.PostgreSQLEnumType;
import eu.csgroup.coprs.monitoring.common.datamodel.Status;
import eu.csgroup.coprs.monitoring.common.datamodel.Level;
import eu.csgroup.coprs.monitoring.common.datamodel.Workflow;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

@Data
@EqualsAndHashCode()
@ToString()
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@TypeDef(name="pgsql_enum", typeClass =  PostgreSQLEnumType.class)
@Embeddable
public class Processing implements DefaultEntity, Serializable {
    @Transient
    @Serial
    private static final long serialVersionUID = -311807617227639758L;


    @Id
    @SequenceGenerator(sequenceName="external_input_id_seq", name = "external_input_id_seq", allocationSize=1)
    @GeneratedValue(generator = "external_input_id_seq", strategy = GenerationType.SEQUENCE)
    private Long id;

    private String mission;

    @Column(name="rs_chain_name")
    private String rsChainName;

    @Column(name="rs_chain_version")
    private String rsChainVersion;

    @Enumerated(EnumType.STRING)
    @Type(type = "pgsql_enum")
    private Workflow workflow;

    @Enumerated(EnumType.STRING)
    @Type(type = "pgsql_enum")
    private Level level;

    @Enumerated(EnumType.STRING)
    @Type(type = "pgsql_enum")
    private Status status;

    @Column(name="processing_date")
    private Instant processingDate;

    @Column(name="end_sensing_date")
    private Instant endSensingDate;

    @Column(name="t0_pdgs_date")
    private Instant t0PdgsDate;

    private boolean duplicate;


    @Override
    public Processing copy() {
        return this.toBuilder().build();
    }

    @Override
    public void resetId() {
        this.id = null;
    }
}