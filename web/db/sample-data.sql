--
-- PostgreSQL database dump
--

-- Dumped from database version 9.2.2
-- Dumped by pg_dump version 9.2.2
-- Started on 2013-03-30 18:51:57 EDT

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

SET search_path = public, pg_catalog;

--
-- TOC entry 2210 (class 0 OID 24912)
-- Dependencies: 171
-- Data for Name: songs; Type: TABLE DATA; Schema: public; Owner: gea
--

INSERT INTO songs (id, artist, album, title, "rdioId") VALUES (1, 'Meshuggah', 'Koloss', 'I Am Colossus', 't15858227');
INSERT INTO songs (id, artist, album, title, "rdioId") VALUES (2, 'Meshuggah', 'Koloss', 'The Demon''s Name Is Surveillance', 't15858247');
INSERT INTO songs (id, artist, album, title, "rdioId") VALUES (3, 'Meshuggah', 'Koloss', 'Do Not Look Down', 't15858265');
INSERT INTO songs (id, artist, album, title, "rdioId") VALUES (4, 'Meshuggah', 'ObZen', 'Combustion', 't308719');
INSERT INTO songs (id, artist, album, title, "rdioId") VALUES (5, 'Meshuggah', 'ObZen', 'Electric Red', 't308730');
INSERT INTO songs (id, artist, album, title, "rdioId") VALUES (6, 'Meshuggah', 'ObZen', 'Bleed', 't308745');
INSERT INTO songs (id, artist, album, title, "rdioId") VALUES (7, 'Volbeat', 'Beyond Hell / Above Heaven', 'The Mirror And The Ripper', 't16401310');
INSERT INTO songs (id, artist, album, title, "rdioId") VALUES (8, 'Volbeat', 'Beyond Hell / Above Heaven', 'Heaven Nor Hell', 't16401332');
INSERT INTO songs (id, artist, album, title, "rdioId") VALUES (9, 'Volbeat', 'Beyond Hell / Above Heaven', 'Who They Are', 't16401382');
INSERT INTO songs (id, artist, album, title, "rdioId") VALUES (10, 'Volbeat', 'Guitar Gangsters & Cadillac Blood', 'Intro (End Of The Road)', 't3308868');
INSERT INTO songs (id, artist, album, title, "rdioId") VALUES (11, 'Volbeat', 'Guitar Gangsters & Cadillac Blood', 'Guitar Gangsters & Cadillac Blood', 't3308870');
INSERT INTO songs (id, artist, album, title, "rdioId") VALUES (12, 'Volbeat', 'Guitar Gangsters & Cadillac Blood', 'Back To Prom', 't3308875');


--
-- TOC entry 2212 (class 0 OID 24929)
-- Dependencies: 173
-- Data for Name: ratings; Type: TABLE DATA; Schema: public; Owner: gea
--

INSERT INTO ratings (id, sid, rating, "time") VALUES (1, 1, 1, '2013-03-30 18:46:31.917881');
INSERT INTO ratings (id, sid, rating, "time") VALUES (2, 2, 1, '2013-03-30 18:47:07.884791');
INSERT INTO ratings (id, sid, rating, "time") VALUES (3, 3, 1, '2013-03-30 18:47:21.513034');
INSERT INTO ratings (id, sid, rating, "time") VALUES (4, 4, 1, '2013-03-30 18:47:33.424182');
INSERT INTO ratings (id, sid, rating, "time") VALUES (5, 5, 1, '2013-03-30 18:47:59.6563');
INSERT INTO ratings (id, sid, rating, "time") VALUES (6, 6, 1, '2013-03-30 18:48:16.822093');
INSERT INTO ratings (id, sid, rating, "time") VALUES (7, 7, -1, '2013-03-30 18:48:33.901092');
INSERT INTO ratings (id, sid, rating, "time") VALUES (8, 8, -1, '2013-03-30 18:48:45.470382');
INSERT INTO ratings (id, sid, rating, "time") VALUES (9, 9, -1, '2013-03-30 18:49:00.193444');
INSERT INTO ratings (id, sid, rating, "time") VALUES (10, 10, -1, '2013-03-30 18:49:10.752677');
INSERT INTO ratings (id, sid, rating, "time") VALUES (11, 11, -1, '2013-03-30 18:49:22.3177');
INSERT INTO ratings (id, sid, rating, "time") VALUES (12, 12, -1, '2013-03-30 18:49:33.491916');


--
-- TOC entry 2217 (class 0 OID 0)
-- Dependencies: 172
-- Name: ratings_id_seq; Type: SEQUENCE SET; Schema: public; Owner: gea
--

SELECT pg_catalog.setval('ratings_id_seq', 12, true);


--
-- TOC entry 2218 (class 0 OID 0)
-- Dependencies: 170
-- Name: songs_id_seq; Type: SEQUENCE SET; Schema: public; Owner: gea
--

SELECT pg_catalog.setval('songs_id_seq', 12, true);


-- Completed on 2013-03-30 18:51:57 EDT

--
-- PostgreSQL database dump complete
--

