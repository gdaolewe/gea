// requirements
var _ = require('underscore'),
    pg = require(__appDir + '/db/geaPg'),
    async = require('async'),
    fs = require('fs'),
    path = require('path'),
    util = require('util'),
    request = require('request'),
    rdio = require('./util/rdio');

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

// routes
module.exports = {
  // POST /rate?from=<rdio>&id=<id>&verdict=<like|dislike>
  post: function (req, res) {
    var rdioId = req.query.id;
    var verdict = req.query.verdict;
    pg.connect(function (err, client, done) {
      async.waterfall([
        function (next) {
          if (err) {
            return next(err);
          }
          if (!rdioId) {
            return next(new Error('id must be specified'));
          }
          if (!verdict) {
            return next(new Error('verdict must be specified'));
          }
          next();
        },
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
              var icon = data.result[rdioId].icon;
              client.query({
                name: 'insert_song',
                text: INSERT_SONG,
                values: [artist, album, title, rdioId, icon]
              }, next);
            })
          }
        },
        function (data, next) {
          request({url: 'http://freegeoip.net/json/' + req.ip}, function (err, resp, body) {
            client.query({
              name: 'insert_rating',
              text: INSERT_RATING,
              values: [data.rows[0].id, VERDICTS[req.query.verdict], JSON.parse(body).region_name]
            }, next);
          });
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
  // GET /rate?[artist=<artist>[&album=<album>&title=<title>]][[&pastHours=<pastHours>]|[&timeStart=<timeStart>&timeEnd=<timeEnd>]][&limit=<limit>&offset=<offset>]
  // Artist is optional
  // If artist is specified, then album || title can be specified.
  // pastHours || (timeStart && timeEnd) are optional, pastHours takes precedent -- default is 24 hours
  // limit will limit the number of results (defaults to 10, maximum 100)
  // CURRENTLY UNIMPLEMENTED: offset will skip the first given number of results, useful for pagination
  get: function (req, res) {
    // Reference all parameters
    var artist = req.query.artist;
    var album = req.query.album;
    var title = req.query.title;
    var pastHours = req.query.pastHours;
    var timeStart = req.query.timeStart;
    var timeEnd = req.query.timeEnd;
    var limit = req.query.limit || '10';
    // var offset = req.query.offset || '0';
    // var order = req.query.asc ? 'ASC' : 'DESC';

    // Construct extra part of query
    var p = 0;
    var extraWhere = ''; // Extra where query stuff
    var queryName = 'get_ratings';
    var vals = [];

    // Construct meta part of query
    if (artist) {
      extraWhere += util.format(' AND LOWER(artist) = LOWER($%d) ', ++p);
      vals.push(artist);
      queryName += '_artist';
      if (album) {
        extraWhere += util.format(' AND LOWER(album) = LOWER($%d) ', ++p);
        vals.push(album);
        queryName += '_album';
      }
      if (title) {
        extraWhere += util.format(' AND LOWER(title) = LOWER($%d) ', ++p);
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
      extraWhere += util.format(' AND time > NOW() - INTERVAL \'%d HOURS\'', pastHours);
    } else if (timeStart && timeEnd) {
      extraWhere += util.format(' AND time BETWEEN $%d AND $%d', ++p, ++p);
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
    }

    // Insert extra stuff into query
    var query = GET_RATINGS
                .replace('{EXTRA_WHERE}', extraWhere)
                .replace('{LIM}', '$' + ++p);
                // .replace('{OFF}', '$' + ++p)
                // .replace('{ORDER}', order);
    vals.push(limit);
    //vals.push(offset);
    // queryName += '_' + order;

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
        res.json(_.groupBy(data.rows, function (item) {
          var c = item.coords;
          delete item.coords;
          return c;
        }));
        done();
      });
    });
  }
};
