var dbm = require('db-migrate');
var type = dbm.dataType;

exports.up = function(db, callback) {
  db.addColumn('songs', 'image', {type: type.STRING, length: 255}, callback);
};

exports.down = function(db, callback) {
  db.removeColumn('songs', 'image', callback);
};
