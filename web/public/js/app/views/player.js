/*global define*/
define([
  'backbone',
  'promise',
  'jqueryrdio',
  'util/jqr!'
], function (
  bb,
  promise
) {
  var playing = 0;
  var song = null;
  var playingText = ['Play', 'Pause'];
  var loading = false;
  var deferred = new promise.Promise();

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
      // Cache selectors
      this.$art = this.$('#player-art');
      this.$track = this.$('#track');
      this.$artist = this.$('#artist');
      this.$album = this.$('#album');
      // Player parameters
      this.duration = 1;
      this.trackPosition = 0;
      this.sourcePosition = 0;
      this.$api = this.$('#api');
      // Start playing the album with id a171827 when ready
      this.$api.bind('ready.rdio', $.proxy(function() {
        this.$api.rdio().queue('a171827');
      }, this));

      // When the playing track has changed, adjust the album art, track, album, and artist info accordingly
      this.$api.bind('playingTrackChanged.rdio', $.proxy(function(e, playingTrack, sourcePosition) {
        deferred.done();
        if (playingTrack) {
          this.sourcePosition = sourcePosition;
          //Update the play/pause button correctly
          deferred.then($.proxy(function () {
            this.updatePlayPauseButton();
          }), this);
          // Save the current track's duration for computing the percent remaining
          this.duration = playingTrack.duration;
          // Update the album art div to reflect the current track's art
          this.$art.attr('src', playingTrack.icon);
          // Update the currently playing track name
          this.$track.text(playingTrack.name);
          // Update the currently playing track's artist name
          this.$artist.text(playingTrack.artist);
          // Update the currently playing track's album name
          this.$album.text(playingTrack.album);
        }
      }, this));

      // When the play-state has changed, adjust the play/pause button appropriately
      this.$api.bind('playStateChanged.rdio', $.proxy(function(e, playState) {
        deferred.then($.proxy(function () {
            this.updatePlayPauseButton();
            //If the playState is "playing" (==1), we are no longer waiting for the track to load
            if (playState === 1) loading = false;
          }), this);
      }, this));

      // Update the progress bar fill amount
      this.$api.bind('positionChanged.rdio', $.proxy(function(e, position) {
        // When Rdio calls the positionChanged callback function, adjust the width of the progress bar's "fill" to
        // scale according the percent played of the track's duration
        this.$progressBarFill.css('width', Math.floor(100*position/this.duration)+'%');
        this.trackPosition = position;
      }, this));

      $.get('/rdio/getPlaybackToken', $.proxy(function (data) {
        this.$api.rdio(data.result);
      }, this));
      this.$playPauseButton = this.$('#play-pause');
      this.updatePlayPauseButton();
      this.$progressBarFill = this.$('#fill');
    },
    togglePlay: function (e) {
      e.stopPropagation();
      e.preventDefault();
      deferred.then($.proxy(function () {
        //If we're still waiting to load from Rdio, don't handle this.
        if (loading) return;
        ++playing;
        playing % 2 ? this.$api.rdio().play() : this.$api.rdio().pause();
      }, this));
    },
    playNext: function (e) {
      e.stopPropagation();
      e.preventDefault();
      deferred.then($.proxy(function () {
        //If we're still waiting to load from Rdio, don't handle this.
        if (loading) return;
        playing = 1;
        loading = true;
        this.$api.rdio().next();
      }, this));
    },
    playPrevious: function (e) {
      e.stopPropagation();
      e.preventDefault();
      deferred.then($.proxy(function () {
        //If we're still waiting to load from Rdio, don't handle this.
        if (loading) return;
        playing = 1;
        //If we've played more than 5 seconds (or this is the first track)
        // manually seek to the beginning of the current track instead of changing to the previous track
        if (this.trackPosition >= 5 || this.sourcePosition === 0) {
          this.$api.rdio().seek(0);
        } else {
          //Otherwise, seek to the beginning of this track and then change to the previous (ensures the right behavior)
          this.$api.rdio().seek(0);
          loading = true;
          this.$api.rdio().previous();
        }
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
    updatePlayPauseButton: function () {
      this.$playPauseButton.text(playingText[playing % 2]);
    }
  }))();
});
