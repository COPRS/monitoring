CREATE TYPE trace_type AS ENUM
(
    'REPORT'
);

CREATE TYPE trace_level AS ENUM
(
    'INFO',
    'WARNING',
    'ERROR',
    'DEBUG',
    'FATAL'
);

CREATE TYPE mission AS ENUM
(
    'S1',
    'S2',
    'S3'
);

CREATE TYPE workflow AS ENUM
(
    'NOMINAL',
    'EXTERNAL_DEMAND',
    'EXTERNAL_CUSTOM_DEMAND',
    'OPERATOR_DEMAND'
);

CREATE TYPE event AS ENUM
(
    'begin',
    'end'
);

CREATE TYPE satellite AS ENUM
(
    'S1A',
    'S1B',
    'S1C',
    'S2A',
    'S2B',
    'S5'
);

CREATE TABLE header
(
    id bigserial PRIMARY KEY,
    timestamp TIMESTAMP,
    level trace_level,
    mission mission,
    workflow workflow,
    debugMode Boolean,
    tagList text[]
);

CREATE TABLE task
(
    id bigserial PRIMARY KEY,
    uid text[],
    name text[],
    event event,
    dataRateMebibytesSec double,
    dataVolumeMebibytes double,
    satellite satellite,
    input json
);

CREATE TABLE trace
(
    id bigserial PRIMARY KEY,
    headerId bigint NOT NULL REFERENCES header(id) ON DELETE CASCADE
    taskId bigint NOT NULL REFERENCES task(id) ON DELETE CASCADE
    custom json,
    kubernetes json
);
