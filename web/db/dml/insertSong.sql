INSERT INTO songs (artist, album, title, "rdioId")
VALUES ($1, $2, $3, $4)
RETURNING id
