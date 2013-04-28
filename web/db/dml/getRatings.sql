--SELECT artist, album, title, "rdioId", SUM(rating) as score
--FROM songs, ratings
--WHERE songs.id = ratings.sid{--EXTRA_WHERE--}
--GROUP BY artist, album, title, "rdioId"
--ORDER BY score {--ORDER--}
--LIMIT {--LIM--} OFFSET {--OFF--}
SELECT coords, artist, album, title, "rdioId", image, score FROM (
  SELECT coords, artist, album, title, "rdioId", image, SUM(rating) as score, row_number() OVER (PARTITION BY locations.id) AS rank
  FROM songs, ratings, locations
  WHERE songs.id = ratings.sid AND ratings.lid = locations.id{EXTRA_WHERE}
  GROUP BY locations.id, coords, artist, album, title, "rdioId", image
) scored_songs_by_lid
WHERE rank <= {LIM}
ORDER BY score DESC
