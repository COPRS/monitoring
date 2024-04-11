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

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import eu.csgroup.coprs.monitoring.common.bean.AutoMergeableMap;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class Product implements DefaultEntity, Serializable {
    @Transient
    @Serial
    private static final long serialVersionUID = -1088870334322071348L;


    @Id
    @SequenceGenerator(sequenceName="product_id_seq", name = "product_id_seq", allocationSize=1)
    @GeneratedValue(generator = "product_id_seq", strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false, unique = true)
    private String filename;

    @Type( type = "jsonb" )
    @Column(columnDefinition = "jsonb")
    private AutoMergeableMap custom;

    private String timelinessName;

    private int timelinessValueSeconds;

    private boolean endToEndProduct;

    @Column(name = "t0_pdgs_date")
    private Instant t0PdgsDate;

    private Instant generationBeginDate;

    private Instant generationEndDate;

    private Instant catalogStorageBeginDate;

    private Instant catalogStorageEndDate;

    private Instant pripStorageBeginDate;

    private Instant pripStorageEndDate;

    private Instant qualityCheckBeginDate;

    private Instant qualityCheckEndDate;

    private Instant firstDownloadDate;

    private Instant evictionDate;

    @Override
    public Product copy() {
        return this.toBuilder().build();
    }

    @Override
    public void resetId() {
        this.id = null;
    }
}