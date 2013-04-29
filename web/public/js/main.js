/*global require _*/
require({
  packages: ['app'],
  paths: {
    'jquery': [
      '//cdnjs.cloudflare.com/ajax/libs/jquery/2.0.0/jquery.min',
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
    'cookie': [
      '//cdnjs.cloudflare.com/ajax/libs/jquery-cookie/1.3.1/jquery.cookie.min',
      'lib/jquery.cookie'
    ],
    'jqueryrdio': 'lib/jquery.rdio.min',
    'promise': 'lib/promise',
    'async': 'lib/async',
    'text': 'lib/text',
    'oms': 'lib/oms.min',
    'joyride': 'lib/jquery.joyride',
    'keymaster': 'lib/keymaster.min',
    'backboneshortcuts': 'lib/backbone.shortcuts.min',
    'mc': 'lib/markerclusterer_packed'
  },
  shim: {
    'backbone': {
      deps: ['jquery', 'util/_conf'],
      exports: 'Backbone'
    },
    'jqueryrdio': ['jquery'],
    'joyride': ['jquery', 'cookie'],
    'cookie': ['jquery'],
    'oms': ['util/googlemaps'],
    'backboneshortcuts': ['backbone', 'keymaster'],
    'mc': ['util/googlemaps']
  },
  deps: ['app']
});
