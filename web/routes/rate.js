// requirements
var pg = require('pg'),
    async = require('async'),
    fs = require('fs'),
    path = require('path'),
    util = require('util'),
    rdio = require('./util/rdio'),
    config = require(path.join(__appDir, 'db/database.json'))[process.env.NODE_ENV === 'production' ? 'prod' : 'dev'];

// import sql queries
const DML_DIR = path.join(__appDir, 'db/dml');
const GET_SONG = fs.readFileSync(path.join(DML_DIR, 'getSong.sql'), 'utf8');
const INSERT_SONG = fs.readFileSync(path.join(DML_DIR, 'insertSong.sql'), 'utf8');
const INSERT_RATING = fs.readFileSync(path.join(DML_DIR, 'insertRating.sql'), 'utf8');
const GET_RATINGS = fs.readFileSync(path.join(DML_DIR, 'getRatings.sql'), 'utf8');

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
          if (err) {
            return next(err);
          }
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
  },
  // GET / recieve?artist=<artist>&album=<album>&title=<title>&pastHours=<pastHours>&timeStart=<timeStart>&timeEnd=<timeEnd>
  get: function (req, res) {
    // Reference all parameters
    var artist = req.query.artist;
    var album = req.query.album;
    var title = req.query.title;
    var pastHours = req.query.pastHours;
    var timeStart = req.query.timeStart;
    var timeEnd = req.query.timeEnd;

    // Construct extra part of query
    var p = 0;
    var extra = ''; // Extra query stuff
    var queryName = 'get_ratings';
    var vals = [];

    // Construct meta part of query
    if (artist) {
      extra += util.format(' AND LOWER(artist) = LOWER($%d) ', ++p);
      vals.push(artist);
      queryName += '_artist';
      if (album) {
        extra += util.format(' AND LOWER(album) = LOWER($%d) ', ++p);
        vals.push(album);
        queryName += '_album';
      }
      if (title) {
        extra += util.format(' AND LOWER(title) = LOWER($%d) ', ++p);
        vals.push(title);
        queryName += '_title';
      }
    }

    // Construct time part of query
    if (pastHours) {
      pastHours = parseInt(pastHours);
      if (isNaN(pastHours)) {
        return res.json(500, {err: 'pastHours must be a number!'});
      }
      // pastHours is guaranteed to be a number, no risk of SQL injection
      extra += util.format(' AND time > NOW() - INTERVAL \'%d HOURS\'', pastHours);
    } else if (timeStart && timeEnd) {
      extra += util.format(' AND time BETWEEN $%d AND $%d', ++p, ++p);
      var timeStartInt = parseInt(timeStart);
      var timeEndInt = parseInt(timeEnd);
      var start, end;
      if (isNaN(timeStartInt) || isNaN(timeEndInt)) {
        start = new Date(timeStart);
        end = new Date(timeEnd);
      } else {
        start = new Date(timeStartInt);
        end = new Date(timeEndInt);
      }
      vals.push(start);
      vals.push(end);
      queryName += '_startTime_endTime';
    } else {
      // Default to last 24 hours
      extra += ' AND time > NOW() - INTERVAL \'24 HOUR\'';
    }

    // Insert extra into query
    var query = GET_RATINGS.replace('{EXTRA}', extra);

    // execute query
    pg.connect(function (err, client, done) {
      client.query({
        text: query,
        name: queryName,
        values: vals
      }, function (err, data) {
        if (err) {
          res.json(500, err);
          return done();
        }
        res.json(data.rows);
        done();
      });
    });
  }
};
