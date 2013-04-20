-- Table: locations

-- DROP TABLE locations;

CREATE TABLE locations
(
  id serial NOT NULL,
  name character varying(128),
  CONSTRAINT pkey_locations PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE locations
  OWNER TO gea;

-- Index: idx_name

-- DROP INDEX idx_name;

CREATE INDEX idx_name
  ON locations
  USING hash
  (name COLLATE pg_catalog."default");

-- Insert locations
INSERT INTO locations (id, name) VALUES
  (1, 'Alabama'),
  (2, 'Alaska'),
  (3, 'Arizona'),
  (4, 'Arkansas'),
  (5, 'California'),
  (6, 'Colorado'),
  (7, 'Connecticut'),
  (8, 'Delaware'),
  (9, 'Florida'),
  (10, 'Georgia'),
  (11, 'Hawaii'),
  (12, 'Idaho'),
  (13, 'Illinois'),
  (14, 'Indiana'),
  (15, 'Iowa'),
  (16, 'Kansas'),
  (17, 'Kentucky'),
  (18, 'Louisiana'),
  (19, 'Maine'),
  (20, 'Maryland'),
  (21, 'Massachusetts'),
  (22, 'Michigan'),
  (23, 'Minnesota'),
  (24, 'Mississippi'),
  (25, 'Missouri'),
  (26, 'Montana'),
  (27, 'Nebraska'),
  (28, 'Nevada'),
  (29, 'New Hampshire'),
  (30, 'New Jersey'),
  (31, 'New Mexico'),
  (32, 'New York'),
  (33, 'North Carolina'),
  (34, 'North Dakota'),
  (35, 'Ohio'),
  (36, 'Oklahoma'),
  (37, 'Oregon'),
  (38, 'Pennsylvania'),
  (39, 'Rhode Island'),
  (40, 'South Carolina'),
  (41, 'South Dakota'),
  (42, 'Tennessee'),
  (43, 'Texas'),
  (44, 'Utah'),
  (45, 'Vermont'),
  (46, 'Virginia'),
  (47, 'Washington'),
  (48, 'West Virginia'),
  (49, 'Wisconsin'),
  (50, 'Wyoming');

-- Add lid column
ALTER TABLE ratings
 ADD COLUMN lid integer NOT NULL DEFAULT 11;

-- Add foreign key
ALTER TABLE ratings
  ADD CONSTRAINT fkey_lid FOREIGN KEY (lid) REFERENCES locations (id)
   ON UPDATE NO ACTION ON DELETE NO ACTION;
CREATE INDEX fki_fkey_lid
  ON ratings(lid);
