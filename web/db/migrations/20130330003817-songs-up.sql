-- Table: songs

CREATE TABLE songs
(
  id serial NOT NULL,
  artist character varying(255),
  album character varying(255),
  title character varying(255),
  "rdioId" character varying(16),
  CONSTRAINT pkey PRIMARY KEY (id),
  CONSTRAINT "uq_rdioId" UNIQUE ("rdioId")
);

-- Index: idx_stat_group
-- Used for
CREATE INDEX idx_stat_group
  ON songs
  USING btree
  (artist COLLATE pg_catalog."default", album COLLATE pg_catalog."default");
ALTER TABLE songs CLUSTER ON idx_stat_group;

-- Index: idx_title

CREATE INDEX idx_title
  ON songs
  USING hash
  (title COLLATE pg_catalog."default");

-- Index: "idx_rdioId"

CREATE INDEX "idx_rdioId"
  ON songs
  USING hash
  ("rdioId" COLLATE pg_catalog."default");
