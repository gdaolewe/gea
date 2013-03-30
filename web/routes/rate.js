// requirements
var pg = require('pg'),
    async = require('async'),
    fs = require('fs'),
    path = require('path'),
    util = require('util'),
    rdio = require('./util/rdio'),
    config = require(path.join(__appDir, 'db/database.json'))[process.env.NODE_ENV === 'production' ? 'prod' : 'dev'];

// import sql queries
const DML_DIR = path.join(__dirname, '../db/dml');
const GET_SONG = fs.readFileSync(path.join(DML_DIR, 'getSong.sql'), 'utf8');
const INSERT_SONG = fs.readFileSync(path.join(DML_DIR, 'insertSong.sql'), 'utf8');
const INSERT_RATING = fs.readFileSync(path.join(DML_DIR, 'insertRating.sql'), 'utf8');

// verdicts definition
const VERDICTS = {
  like: 1,
  dislike: -1
};

// pg configuration
pg.defaults.user = config.user;
pg.defaults.password = config.password;
pg.defaults.host = config.host;
pg.defaults.port = config.port;
pg.defaults.database = config.database;
pg.defaults.poolSize = config.max_connections;

// routes
module.exports = {
  // POST /rate?from=<rdio>&id=<id>&verdict=<like|dislike>
  post: function (req, res) {
    var rdioId = req.query.id;
    pg.connect(function (err, client, done) {
      async.waterfall([
        function (next) {
          client.query({
            name: 'get_song',
            text: GET_SONG,
            values: [rdioId]
          }, next);
        },
        function (data, next) {
          if (data.rows.length > 0) {
            // Insert rating
            console.log('exists');
            next(null, data);
          } else {
            console.log('inserting song');
            // get information from rdio
            rdio.doUnauthenticatedRequest({
              method: 'get',
              keys: rdioId
            }, function (err, data) {
              if (err) {
                return next(err);
              }
              if (data.status !== 'ok') {
                return next(data);
              }
              // insert song into database
              var artist = data.result[rdioId].artist;
              var album = data.result[rdioId].album;
              var title = data.result[rdioId].name;
              client.query({
                name: 'insert_song',
                text: INSERT_SONG,
                values: [artist, album, title, rdioId]
              }, next);
            })
          }
        },
        function (data, next) {
          client.query({
            name: 'insert_rating',
            text: INSERT_RATING,
            values: [data.rows[0].id, VERDICTS[req.query.verdict]]
          }, next);
        }
      ], function (err, data) {
        if (err) {
          res.json(500, err);
        } else {
          res.json(data.rows);
        }
        done();
      });
    });
  }
};
