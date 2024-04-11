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

ALTER TABLE external_input
    ADD COLUMN catalog_storage_begin_date TIMESTAMP;
ALTER TABLE external_input
    RENAME COLUMN pickup_point_seen_date TO seen_date;
ALTER TABLE external_input
    RENAME COLUMN pickup_point_available_date TO available_date;
ALTER TABLE external_input
    RENAME COLUMN catalog_storage_date TO catalog_storage_end_date;

ALTER TABLE product
    ADD COLUMN generation_begin_date TIMESTAMP,
    ADD COLUMN generation_end_date TIMESTAMP,
    ADD COLUMN catalog_storage_begin_date TIMESTAMP,
    ADD COLUMN catalog_storage_end_date TIMESTAMP,
    ADD COLUMN prip_storage_begin_date TIMESTAMP,
    ADD COLUMN quality_check_begin_date TIMESTAMP,
    ADD COLUMN quality_check_end_date TIMESTAMP,
    ADD COLUMN first_download_date TIMESTAMP,
    ADD COLUMN eviction_date TIMESTAMP;
ALTER TABLE product
    RENAME COLUMN prip_storage_date TO prip_storage_end_date;