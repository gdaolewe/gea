// Utility which interacts with rdio

// Requirements
var OAuth = require('oauth').OAuth,
    path = require('path');

// Read in Rdio config
var rdioConfig = require(path.join(__appDir, 'config/rdio.json'));
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

// Executes a signed request to rdio with no authentication
// args is a hash with method set to the method and all other keys set to rdio arguments
// callback should accept err and data
exports.doUnauthenticatedRequest = function (args, callback) {
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
};
