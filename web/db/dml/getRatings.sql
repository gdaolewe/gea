--SELECT artist, album, title, "rdioId", SUM(rating) as score
--FROM songs, ratings
--WHERE songs.id = ratings.sid{--EXTRA_WHERE--}
--GROUP BY artist, album, title, "rdioId"
--ORDER BY score {--ORDER--}
--LIMIT {--LIM--} OFFSET {--OFF--}
SELECT name, artist, album, title, "rdioId", score FROM (
  SELECT name, artist, album, title, "rdioId", SUM(rating) as score, row_number() OVER (PARTITION BY name) AS rank
  FROM songs, ratings, locations
  WHERE songs.id = ratings.sid AND ratings.lid = locations.id{EXTRA_WHERE}
  GROUP BY name, artist, album, title, "rdioId"
) scored_songs_by_lid
WHERE rank <= {LIM}
ORDER BY name, score DESC
