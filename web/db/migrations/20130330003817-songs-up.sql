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

-- Index: idx_album

CREATE INDEX idx_album
  ON songs
  USING hash
  (album COLLATE pg_catalog."default");

-- Index: idx_artist

CREATE INDEX idx_artist
  ON songs
  USING hash
  (artist COLLATE pg_catalog."default");

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
