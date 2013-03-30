SELECT artist, album, title, SUM (rating)
FROM songs, ratings
WHERE songs.id = ratings.sid{EXTRA}
GROUP BY artist, album, title
