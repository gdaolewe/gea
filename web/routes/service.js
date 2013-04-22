// Requirements
var rdio = require('./util/rdio');

// These routes deal with streaming services
// Right now, we only support Rdio.

// Rdio routes
exports.rdio = {
  getPlaybackToken: function (domain) {
    return function (req, res) {
      // Get playback token from rdio and respond to client
      rdio.doUnauthenticatedRequest({
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
    rdio.doUnauthenticatedRequest(args, basicResponder(res));
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
