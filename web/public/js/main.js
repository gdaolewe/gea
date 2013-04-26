/*global require _*/
require({
  packages: ['app'],
  paths: {
    'jquery': [
      '//cdnjs.cloudflare.com/ajax/libs/jquery/1.9.1/jquery.min',
      'lib/jquery.min'
    ],
    '_': [
      '//cdnjs.cloudflare.com/ajax/libs/lodash.js/1.0.0-rc.3/lodash.min',
      'lib/lodash.min'
    ],
    'backbone': [
      '//cdnjs.cloudflare.com/ajax/libs/backbone.js/0.9.10/backbone-min',
      'lib/backbone.min'
    ],
    'jqueryrdio': 'lib/jquery.rdio.min',
    'promise': 'lib/promise',
    'async': 'lib/async',
    'text': 'lib/text',
    'oms': 'lib/oms.min',
    'keymaster': 'lib/keymaster.min',
    'backboneshortcuts': 'lib/backbone.shortcuts.min'
  },
  shim: {
    'backbone': {
      deps: ['jquery', 'util/_conf'],
      exports: 'Backbone'
    },
    'jqueryrdio': ['jquery'],
    'oms': ['util/googlemaps'],
    'backboneshortcuts': ['backbone', 'keymaster']
  },
  deps: ['app']
});
