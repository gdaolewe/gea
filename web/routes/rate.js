// requirements
var pg = require('pg'),
    async = require('async'),
    q = require('q'),
    util = require('util'),
    config = require('../db/database.json')[process.env.NODE_ENV === 'production' ? 'prod' : 'dev'];

// verdicts definition
const VERDICTS = {
  like: 1,
  dislike: -1
};

// Defer queries until we're ready
var deferred = q.defer();

// pg client
var conString = util.format('tcp://%s:%s@%s:%d/%s', config.user, config.password, config.host, config.port, config.database);
var client = new pg.Client(conString);

// Connect and resolve deferred
client.connect(function (err) {
  if (err) {
    throw err;
  }
  deferred.resolve();
});

// Queue for processing SQL queries
var workQueue = async.queue(function (task, callback) {
  deferred.promise.then(function () {
    task.connected = true;
    callback();
  }, config['max_connections']);
});

module.exports = {
  // POST /rate?from=<rdio>&id=<id>&verdict=<like|dislike>
  post: function (req, res) {
    var task = {now: new Date().getTime(), query: req.query};
    workQueue.push(task, function (err) {
      res.json(task);
    });
  }
};