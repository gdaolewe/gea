-- Table: ratings

-- DROP TABLE ratings;

CREATE TABLE ratings
(
  id serial NOT NULL,
  sid integer,
  rating smallint,
  "time" timestamp without time zone DEFAULT now(),
  CONSTRAINT pkey_rating PRIMARY KEY (id),
  CONSTRAINT fkey_sid FOREIGN KEY (sid)
      REFERENCES songs (id) MATCH SIMPLE
      ON UPDATE CASCADE ON DELETE CASCADE
);

-- Index: idx_sid

CREATE INDEX idx_sid
  ON ratings
  USING btree
  (sid);
ALTER TABLE ratings CLUSTER ON idx_sid;

-- Index: idx_time

CREATE INDEX idx_time
  ON ratings
  USING btree
  ("time");
