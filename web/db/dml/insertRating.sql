INSERT INTO ratings (sid, rating, lid)
VALUES ($1, $2, (SELECT COALESCE((SELECT id FROM locations WHERE name = $3), 11)))
RETURNING id
