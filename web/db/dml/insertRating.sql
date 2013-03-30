INSERT INTO ratings (sid, rating)
VALUES ($1, $2)
RETURNING id
