// These routes deal with streaming services
// Right now, we only support Rdio.

// Read in Rdio config
var config = require('../config/rdio.json');

// Test routes
exports.rdioConfig = function (req, res) {
  res.json(config);
};
