var dbm = require('db-migrate'),
    type = dbm.dataType,
    fs = require('fs'),
    path = require('path');

exports.up = function(db, callback) {
  db.addColumn('locations', 'coords', {type: type.STRING, length: 25}, function () {
    db.runSql(fs.readFileSync(path.join(__dirname, '20130428032703-location-coords.sql'), 'utf8'), callback);
  });
};

exports.down = function(db, callback) {
  db.removeColumn('locations', 'coords', callback);
};
