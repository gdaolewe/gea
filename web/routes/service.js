// Requirements
var OAuth = require('oauth').OAuth;

// These routes deal with streaming services
// Right now, we only support Rdio.

// Read in Rdio config
var rdioConfig = require('../config/rdio.json');
rdioConfig.url = 'http://api.rdio.com/1/';

// Create OAuth instance for rdio
var roa = new OAuth(
  'http://api.rdio.com/oauth/request_token',
  'http://api.rdio.com/oauth/access_token',
  rdioConfig.key,
  rdioConfig.secret,
  '1.0',
  null,
  'HMAC-SHA1'
);

// Rdio routes
exports.rdio = {
  getPlaybackToken: function (domain) {
    return function (req, res) {
      // Get playback token from rdio and respond to client
      doUnauthenticatedRdioRequest({
        method: 'getPlaybackToken',
        domain: domain
      }, basicResponder(res));
    }
  },
  search: function (req, res) {
    // Reference parameters
    var q = req.query;
    // Create arguments hash to search Rdio
    var args = {
      method: 'search',
      query: q.query,
      types: q.types || 'Artist, Album, Track'
    };
    if (q.never_or) {
      args.never_or = q.never_or;
    }
    if (q.extras) {
      args.extras = q.extras;
    }
    if (q.start) {
      args.start = q.start;
    }
    if (q.count) {
      args.count = q.count;
    }
    // Search Rdio and respond to client
    doUnauthenticatedRdioRequest(args, basicResponder(res));
  }
};

// Returns function which ends data back to the client while handling errors
function basicResponder(res) {
  return function (err, data) {
    if (err) {
      res.json(500, err);
    } else {
      res.json(data);
    }
  }
}

// Executes a signed request to rdio with no authentication
// args is a hash with method set to the method and all other keys set to rdio arguments
// callback should accept err and data
function doUnauthenticatedRdioRequest(args, callback) {
  roa.post(
    rdioConfig.url,
    null, // No auth token
    null, // No auth secret
    args,
    function (err, data) {
      if (err) {
        callback(err);
      } else {
        callback(err, JSON.parse(data))
      }
    }
  );
}
