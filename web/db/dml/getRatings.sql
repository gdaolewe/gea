SELECT artist, album, title, "rdioId", SUM(rating) as score
FROM songs, ratings
WHERE songs.id = ratings.sid{EXTRA_WHERE}
GROUP BY artist, album, title, "rdioId"
ORDER BY score {ORDER}
LIMIT {LIM} OFFSET {OFF}
