-- Copyright 2023 CS Group
--
-- Licensed under the Apache License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
--
--     http://www.apache.org/licenses/LICENSE-2.0
--
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.

CREATE TYPE workflow AS ENUM
(
    'NOMINAL',
    'EXTERNAL_DEMAND',
    'EXTERNAL_CUSTOM_DEMAND',
    'OPERATOR_DEMAND'
);

CREATE TYPE level AS ENUM
(
    'INFO',
    'WARNING',
    'ERROR',
    'DEBUG',
    'FATAL'
);

CREATE TYPE status AS ENUM
(
    'OK',
    'NOK',
    'TIMEOUT'
);

CREATE TABLE processing
(
    id bigserial PRIMARY KEY,
    mission VARCHAR,
    rs_chain_name VARCHAR,
    rs_chain_version VARCHAR,
    workflow workflow,
    level level,
    status status,
    processing_date TIMESTAMP,
    end_sensing_date TIMESTAMP,
    t0_pdgs_date TIMESTAMP,
    duplicate boolean
);

CREATE TABLE missing_products
(
  processing_failed_id bigint NOT NULL REFERENCES processing(id) ON UPDATE CASCADE ON DELETE CASCADE,
  product_metadata_custom jsonb,
  estimated_count integer,
  duplicate boolean,
  end_to_end_product boolean,
  PRIMARY KEY (processing_failed_id)
);

CREATE TABLE input_list_external
(
    processing_id bigint NOT NULL REFERENCES processing(id) ON UPDATE CASCADE ON DELETE CASCADE,
    external_input_id bigint NOT NULL REFERENCES external_input(id) ON UPDATE CASCADE ON DELETE CASCADE,
    PRIMARY KEY (processing_id, external_input_id)
);

CREATE TABLE input_list_internal
(
    processing_id bigint NOT NULL REFERENCES processing(id) ON UPDATE CASCADE ON DELETE CASCADE,
    product_id bigint NOT NULL REFERENCES product(id) ON UPDATE CASCADE ON DELETE CASCADE,
    PRIMARY KEY (processing_id, product_id)
);

CREATE TABLE output_list
(
    processing_id bigint NOT NULL REFERENCES processing(id) ON UPDATE CASCADE ON DELETE CASCADE,
    product_id bigint NOT NULL REFERENCES product(id) ON UPDATE CASCADE ON DELETE CASCADE,
    PRIMARY KEY (processing_id, product_id)
);