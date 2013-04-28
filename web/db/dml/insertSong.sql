INSERT INTO songs (artist, album, title, "rdioId", image)
VALUES ($1, $2, $3, $4, $5)
RETURNING id
