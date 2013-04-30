const OVER_NINE_THOUSAND = 9001;

var pg = require('./geaPg'),
    async = require('async'),
    util = require('util');

// Return random numbers between 1 and 12
var nextSong = function () {
  return Math.floor(Math.random() * 12) + 1;
};

// Return -1 or 1 at random
var nextRating = function () {
  return Math.round(Math.random()) % 2 === 0 ? -1 : 1;
};

// Return random numbers between 1 and 50
var nextLocation = (function () {
  var i = 1;
  return function () {
    if (++i > 50) {
      i = 1;
    }
    return i;
  }
})();

// Series of async tasks
async.series([
  // Empty songs / ratings
  function (next) {
    console.log('Truncating data.');
    pg.connect(function (err, client, done) {
      client.query('TRUNCATE songs, ratings RESTART IDENTITY', function () {
        done();
        next();
      });
    });
  },
  // Songs
  function (next) {
    console.log('Inserting songs.');
    var query = 'INSERT INTO songs (id, artist, album, title, "rdioId", image) VALUES ($1, $2, $3, $4, $5, $6)';
    var songs = [
      [1, 'Meshuggah', 'Koloss', 'I Am Colossus', 't15858227', 'http://cdn3.rd.io/album/9/2/7/0000000000158729/4/square-200.jpg'],
      [2, 'Meshuggah', 'Koloss', 'The Demon\'s Name Is Surveillance', 't15858247', 'http://cdn3.rd.io/album/9/2/7/0000000000158729/4/square-200.jpg'],
      [3, 'Meshuggah', 'Koloss', 'Do Not Look Down', 't15858265', 'http://cdn3.rd.io/album/9/2/7/0000000000158729/4/square-200.jpg'],
      [4, 'Meshuggah', 'ObZen', 'Combustion', 't308719', 'http://cdn3.rd.io/album/4/6/0/0000000000006064/square-200.jpg'],
      [5, 'Meshuggah', 'ObZen', 'Electric Red', 't308730', 'http://cdn3.rd.io/album/4/6/0/0000000000006064/square-200.jpg'],
      [6, 'Meshuggah', 'ObZen', 'Bleed', 't308745', 'http://cdn3.rd.io/album/4/6/0/0000000000006064/square-200.jpg'],
      [7, 'Periphery', 'Periphery', 'Icarus Lives', 't8616591', 'http://cdn3.rd.io/album/c/3/f/00000000000b1f3c/1/square-200.jpg'],
      [8, 'Periphery', 'Periphery', 'Jetpacks Was Yes', 't8616342', 'http://cdn3.rd.io/album/c/3/f/00000000000b1f3c/1/square-200.jpg'],
      [9, 'Periphery', 'Periphery', 'Totla Mad', 't8616630', 'http://cdn3.rd.io/album/c/3/f/00000000000b1f3c/1/square-200.jpg'],
      [10, 'Periphery', 'Periphery II', 'Facepalm Mute', 't28629330', 'http://cdn3.rd.io/album/9/e/a/0000000000292ae9/1/square-200.jpg'],
      [11, 'Periphery', 'Periphery II', 'Ji', 't28629336', 'http://cdn3.rd.io/album/9/e/a/0000000000292ae9/1/square-200.jpg'],
      [12, 'Periphery', 'Periphery II', 'MAKE TOTAL DESTROY', 't28629370', 'http://cdn3.rd.io/album/9/e/a/0000000000292ae9/1/square-200.jpg']
    ];
    // Insert each song
    var i = 0;
    async.whilst(
      function () {
        return i < songs.length;
      },
      function (cont) {
        pg.connect(function (err, client, done) {
          client.query(query, songs[i], function () {
            i++;
            done();
            cont();
          });
        });
      },
      next
    );
  },
  // Ratings
  function () {
    console.log('Inserting ratings (please wait, inserting 36,004 records).');
    var query = 'INSERT INTO ratings (sid, rating, "time", lid) VALUES ($1, $2, $3, $4)';
    var insert = function (d) {
      var args = [nextSong(), nextRating(), d, nextLocation()];
      pg.connect(function (err, client, done) {
        client.query({
          name: 'insert_rating',
          text: query,
          values: args
        }, function () {
          // Truthy values passed to `done` cause the connection to be destroyed
          // Rather than passing directly as callback, we call it here
          done();
        });
      });
    }
    // Today
    var d = new Date();
    for (var i = 0; i < OVER_NINE_THOUSAND; i++) {
      insert(d);
    }
    // Three days ago
    d = new Date(d.getTime() - 60 * 60 * 24 * 3 * 1000);
    for (var i = 0; i < OVER_NINE_THOUSAND; i++) {
      insert(d);
    }
    // Two weeks ago
    d = new Date(d.getTime() - 60 * 60 * 24 * 11 * 1000);
    for (var i = 0; i < OVER_NINE_THOUSAND; i++) {
      insert(d);
    }
    // Three months ago
    d = new Date(d.getTime() - 60 * 60 * 24 * 73 * 1000);
    for (var i = 0; i < OVER_NINE_THOUSAND; i++) {
      insert(d);
    }
  }
]);

