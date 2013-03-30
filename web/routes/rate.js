// requirements
var pg = require('pg'),
    async = require('async'),
    fs = require('fs'),
    path = require('path'),
    util = require('util'),
    config = require('../db/database.json')[process.env.NODE_ENV === 'production' ? 'prod' : 'dev'];

// import sql queries
const DML_DIR = path.join(__dirname, '../db/dml');
const GET_SONG = fs.readFileSync(path.join(DML_DIR, 'getSong.sql'), 'utf8');

// verdicts definition
const VERDICTS = {
  like: 1,
  dislike: -1
};

// pg connection string
var conString = util.format('tcp://%s:%s@%s:%d/%s', config.user, config.password, config.host, config.port, config.database);
pg.defaults.poolSize = config['max_connections'];

// Queue for processing SQL queries
var workQueue = async.queue(function (task, callback) {
  pg.connect(conString, function (err, client, done) {
    if (err) {
      callback(err);
      done(err);
    }
    client.query(task, callback);
    done();
  });
});

module.exports = {
  // POST /rate?from=<rdio>&id=<id>&verdict=<like|dislike>
  post: function (req, res) {
    workQueue.push({
      name: 'get_song',
      text: GET_SONG,
      values: [req.query.id]
    }, function (err, result) {
      if (err) {
        res.json(err);
        return;
      }
      res.json(result.rows);
    });
  }
};