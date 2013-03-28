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
  getPlaybackToken: function (req, res) {
    roa.post(
      rdioConfig.url,
      null, // Access token isn't needed for this request
      null, // Access token secret isn't needed for this request
      {method: 'getPlaybackToken'},
      function (err, data) {
        res.json(JSON.parse(data));
      }
    );
  }
};
