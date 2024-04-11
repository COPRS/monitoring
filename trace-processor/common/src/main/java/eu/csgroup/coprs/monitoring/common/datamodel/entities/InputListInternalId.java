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
public class InputListInternalId implements Serializable {
    @Transient
    @Serial
    private static final long serialVersionUID = 975851832304147013L;


    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "processing_id")
    private Processing processing;

    //@ManyToOne(cascade = CascadeType.ALL)
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "product_id")
    private Product product;
}
