var pg = require('pg'),
    path = require('path'),
    conf = require('./database.json')[process.env.NODE_ENV === 'production' ? 'prod' : 'dev'];

// pg configuration
pg.defaults.user = conf.user;
pg.defaults.password = conf.password;
pg.defaults.host = conf.host;
pg.defaults.port = conf.port;
pg.defaults.database = conf.database;
pg.defaults.poolSize = conf.max_connections;

// export configured pg
module.exports = pg;
