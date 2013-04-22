/*global define*/
define([
	'app/vent',
	'jqueryrdio',
	'util/jqr!'
], function (vent) {

  return {
  	name: 'rdio',

  	setup: function (api, playBackToken) {
  		// Cache the Rdio API object
  		this.$api = api;
  		this.$api.rdio(playBackToken);
  	},
  	bind: function (apiEvent, apiFunction) {
  		this.$api.bind(apiEvent + '.rdio', apiFunction);
  	},
  	play: function (key) {
  		if (key) this.$api.rdio().play(key);
  		else this.$api.rdio().play()
  	},
  	pause: function () {
  		this.$api.rdio().pause();
  	},
  	next: function () {
  		this.$api.rdio().next();
  	},
  	previous: function () {
  		this.$api.rdio().previous();
  	},
  	seek: function (position) {
  		this.$api.rdio().seek(position);
  	},
  	queue: function(key) {
  		this.$api.rdio().queue(key);
  	}
  };
});
