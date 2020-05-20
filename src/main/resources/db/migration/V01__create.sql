CREATE TABLE FEED (
    SEKVENS_ID bigint primary key,
    FNR_BARN varchar,
    FNR_STONADSMOTTAKER varchar,
    FNR_MOR varchar,
    FNR_FAR varchar,
    DATO_START_NY_BA timestamp(3),
    TYPE varchar not null
);

CREATE SEQUENCE FEED_SEQ INCREMENT BY 1 NO CYCLE;
