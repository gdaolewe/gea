var dbm = require('db-migrate'),
    fs = require('fs'),
    path = require('path');

exports.up = function(db, callback) {
  db.runSql(fs.readFileSync(path.join(__dirname, '20130320193829-locations-up.sql'), 'utf8'), callback);
};

exports.down = function(db, callback) {
  db.removeColumn('ratings', 'lid', function () {
    db.dropTable('locations', callback);
  });
};
