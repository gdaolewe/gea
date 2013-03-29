/*global define*/
define([
  'backbone',
  'jqueryrdio',
  'util/jqr!'
], function (
  bb
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
      this.duration = 1;
      this.$api = this.$('#api');
      // Start playing the album with id a171827 when ready
      this.$api.bind('ready.rdio', $.proxy(function() {
        this.$api.rdio().play('a171827');
      }, this));
      
      // When the playing track has changed, adjust the album art, track, album, and artist info accordingly
      this.$api.bind('playingTrackChanged.rdio', $.proxy(function(e, playingTrack, sourcePosition) {
        if (playingTrack) {
          // Save the current track's duration for computing the percent remaining
          this.duration = playingTrack.duration;
          // Update the album art div to reflect the current track's art
          $('#player-art').attr('src', playingTrack.icon);
          // Update the currently playing track name
          $('#track').text(playingTrack.name);
          // Update the currently playing track's artist name
          $('#artist').text('by ' + playingTrack.artist);
          // Update the currently playing track's album name
          $('#album').text('from ' + playingTrack.album);
        }
      }, this));
      
      // Update the progress bar fill amount
      this.$api.bind('positionChanged.rdio', $.proxy(function(e, position) {
        // When Rdio calls the positionChanged callback function, adjust the width of the progress bar's "fill" to 
        // scale according the percent played of the track's duration
        this.$progressBarFill.css('width', Math.floor(100*position/this.duration)+'%');
      }, this));
      
      $.get('/rdio/getPlaybackToken', $.proxy(function (data) {
        this.$api.rdio(data.result);
      }, this));
      this.$playPauseButton = this.$('#play-pause');
      this.$playPauseButton.text(playingText[playing]);
      this.$progressBarFill = this.$('#fill');
    },
    togglePlay: function (e) {
      e.stopPropagation();
      e.preventDefault();
      this.$playPauseButton.text(playingText[++playing % 2]);
      playing % 2 ? this.$api.rdio().play() : this.$api.rdio().pause() ;
      this.$progressBarFill.css('-webkit-animation-play-state', function (i, v) {
        return v === 'paused' ? 'running' : 'paused';
      });
    },
    playNext: function (e) {
      e.stopPropagation();
      e.preventDefault();
      this.$api.rdio().next();
      playing = 1;
      this.$playPauseButton.text(playingText[playing % 2]);
      this.$progressBarFill.css('-webkit-animation-play-state', 'running');
      /*
      Song.get(songId, $.proxy(function (s) {
        song = s;
        this.render();
      }, this));
      */
    },
    playPrevious: function (e) {
      e.stopPropagation();
      e.preventDefault();
      this.$api.rdio().previous();
      playing = 1;
      this.$playPauseButton.text(playingText[playing % 2]);
      this.$progressBarFill.css('-webkit-animation-play-state', 'running');
      /*
      Song.get(songId, $.proxy(function (s) {
        song = s;
        this.render();
      }, this));
      */
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
      /*$.when($.get('/artist/' + song.get('artist')), $.get('/album/' + song.get('album'))).done($.proxy(function (artist, album) {
        this.$metadata.text(album[0].title + ' - ' + song.get('title') + ' by ' + artist[0].name);
      }, this));*/
    }
  }))();
});
