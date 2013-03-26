/*global define*/
define([
  'backbone',
  '../data/Song',
  'util/jqr!'
], function (
  bb,
  Song
) {
  var playing = 1;
  var song = null;
  var playingText = ['Play', 'Pause'];

  return new (bb.View.extend({
    el: '#player',
    events: {
      'click #play-pause': 'togglePlay',
      'click #next': 'playNext',
      'click #previous': 'playPrevious',
      'click #like': 'like',
      'click #dislike': 'dislike'
    },
    initialize: function () {
      this.$playPauseButton = this.$('#play-pause');
      this.$playPauseButton.text(playingText[playing]);
      this.$progressBarFill = this.$('#fill');
      this.$metadata = this.$('#player-metadata');
      this.$('#player-art').text('Art loaded.');
      Song.get(1, $.proxy(function (s) {
        song = s;
        this.render();
      }, this));
    },
    togglePlay: function (e) {
      e.stopPropagation();
      e.preventDefault();
      this.$playPauseButton.text(playingText[++playing % 2]);
      this.$progressBarFill.css('-webkit-animation-play-state', function (i, v) {
        return v === 'paused' ? 'running' : 'paused';
      });
    },
    playNext: function (e) {
      e.stopPropagation();
      e.preventDefault();
      var songId = song.id;
      if (++songId > 5) {
        songId = 1;
      }
      Song.get(songId, $.proxy(function (s) {
        song = s;
        this.render();
      }, this));
    },
    playPrevious: function (e) {
      e.stopPropagation();
      e.preventDefault();
      var songId = song.id;
      if (--songId < 1) {
        songId = 5;
      }
      Song.get(songId, $.proxy(function (s) {
        song = s;
        this.render();
      }, this));
    },
    like: function (e) {
      e.stopPropagation();
      e.preventDefault();
      console.log('Like!');
    },
    dislike: function (e) {
      e.stopPropagation();
      e.preventDefault();
      console.log('Dislike!');
    },
    render: function () {
      $.when($.get('/artist/' + song.get('artistID')), $.get('/album/' + song.get('albumID'))).done($.proxy(function (artist, album) {
        this.$metadata.text(album[0].title + ' - ' + song.get('title') + ' by ' + artist[0].name);
      }, this));
    }
  }))();
});
