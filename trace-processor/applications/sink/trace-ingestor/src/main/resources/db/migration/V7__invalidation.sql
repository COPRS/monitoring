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

CREATE TYPE responsibility AS ENUM
    (
    'PDGS',
    'E2E'
);

CREATE TABLE invalidation
(
    id bigserial PRIMARY KEY,
    responsibility responsibility,
    update_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    root_cause VARCHAR,
    anomaly_identifier bigint,
    comment VARCHAR,
    label VARCHAR
);

CREATE TABLE invalidation_timeliness
(
    parent_id bigint NOT NULL REFERENCES invalidation(id) ON DELETE CASCADE,
    product_ids bigint[],
    PRIMARY KEY (parent_id)
);

CREATE TABLE invalidation_completeness
(
    parent_id bigint NOT NULL REFERENCES invalidation(id) ON DELETE CASCADE,
    missing_products_ids bigint[],
    PRIMARY KEY (parent_id)
);

CREATE  FUNCTION invalidation_update()
    RETURNS TRIGGER AS $$
BEGIN
    NEW.update_date = now();
RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER invalidation_update_trigger
    BEFORE UPDATE
    ON
        invalidation
    FOR EACH ROW
    EXECUTE PROCEDURE invalidation_update();