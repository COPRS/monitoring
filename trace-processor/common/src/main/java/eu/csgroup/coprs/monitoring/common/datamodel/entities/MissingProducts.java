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

import eu.csgroup.coprs.monitoring.common.bean.AutoMergeableMap;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Data
@EqualsAndHashCode()
@ToString()
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class MissingProducts implements DefaultEntity {

    @Id
    @SequenceGenerator(sequenceName="missing_products_id_seq", name = "missing_products_id_seq", allocationSize=1)
    @GeneratedValue(generator = "missing_products_id_seq", strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "processing_failed_id", referencedColumnName = "id")
    private Processing processing = new Processing();

    @Type( type = "jsonb" )
    @Column(columnDefinition = "jsonb")
    private AutoMergeableMap productMetadataCustom;

    private Integer estimatedCount;

    private boolean endToEndProduct;


    @Override
    public void resetId() {
        this.id = null;
    }

    @Override
    public MissingProducts copy() {
        return this.toBuilder().build();
    }

}