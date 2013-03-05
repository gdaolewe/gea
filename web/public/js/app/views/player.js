/*global define*/
define(['backbone', 'util/jqr!'], function (bb) {
  var playing = 1;
  var song = 1;
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
      this.getSong(song);
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
      if (++song > 5) {
        song = 1;
      }
      this.getSong(song);
    },
    playPrevious: function (e) {
      e.stopPropagation();
      e.preventDefault();
      if (--song < 1) {
        song = 5;
      }
      this.getSong(song);
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
    getSong: function (id) {
      $.get('/song/' + id, $.proxy(function (song) {
        $.when($.get('/artist/' + song.artistID), $.get('/album/' + song.albumID)).done($.proxy(function (artist, album) {
          this.album = album[0].title;
          this.title = song.title;
          this.artist = artist[0].name;
          this.$metadata.text(this.album + ' - ' + this.title + ' by ' + this.artist);
        }, this));
      }, this));
    }
  }))();
});
