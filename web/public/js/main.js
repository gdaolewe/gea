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
    'text': 'lib/text'
  },
  shim: {
    'backbone': {
      deps: ['jquery', 'util/_conf'],
      exports: 'Backbone'
    }
  },
  deps: ['app']
});
