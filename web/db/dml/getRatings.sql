SELECT artist, album, title, SUM(rating) as score
FROM songs, ratings
WHERE songs.id = ratings.sid{EXTRA_WHERE}
GROUP BY artist, album, title
ORDER BY score {ORDER}
LIMIT {LIM} OFFSET {OFF}
