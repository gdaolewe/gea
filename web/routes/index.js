// Fake database
var database = {
  songs: [{
    title: 'Marrow',
    artist: 0,
    album: 0
  }, {
    title: 'We Dominate',
    artist: 1,
    album: 1
  }, {
    title: 'Planetary Duality I: Hideous Revelation',
    artist: 2,
    album: 2
  }, {
    title: 'Planetary Duality II: A Prophecies Fruition',
    artist: 2,
    album: 2
  }, {
    title: 'Lathean (Live)',
    artist: 3,
    album: 3
  }],
  artists: [{
    name: 'Meshuggah'
  }, {
    name: 'RXYZYXR'
  }, {
    name: 'The Faceless'
  }, {
    name: 'Mutiny Within'
  }],
  albums: [{
    title: 'Koloss',
    artist: 0
  }, {
    title: 'LMNTS',
    artist: 1
  }, {
    title: 'Planetary Duality',
    artist: 2
  }, {
    title: 'Mutiny Within (Special Edition)',

  }]
};

// GET /song/:id
exports.song = function (req, res) {
  res.json(database.songs[req.params.id]);
};

// GET /artist/:id
exports.artist = function (req, res) {
  res.json(database.artists[req.params.id]);
};

// GET /albums/:id
exports.album = function (req, res) {
  res.json(database.albums[req.params.id]);
};
