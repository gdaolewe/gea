/*global define*/
define([
  'backbone',
  'promise',
  'app/vent',
  'jqueryrdio',
  'util/jqr!'
], function (
  bb,
  promise,
  vent
) {
  var playing = 0;
  var song = null;
  //var playingText = ['Play', 'Pause'];
  var playingImg = ['images/play.png', 'images/pause.png'];
  var loading = false;
  var deferred = new promise.Promise();
  var currentRdioId = 'a171827';

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
      // Queue the first album when ready
      this.$api.on('ready.rdio', $.proxy(function() {
        this.$api.rdio().queue(currentRdioId);
        // Listen for new songs to play
        vent.on('play-key', $.proxy(function (key) {
          currentRdioId = key;
          this.$api.rdio().play(key);
        }, this));
      }, this));

      // When the playing track has changed, adjust the album art, track, album, and artist info accordingly
      this.$api.on('playingTrackChanged.rdio', $.proxy(function(e, playingTrack, sourcePosition) {
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
          // Trigger reflow event
          this.$el.trigger('reflow');
        }
      }, this));

      // When the play-state has changed, adjust the play/pause button appropriately
      this.$api.on('playStateChanged.rdio', $.proxy(function(e, playState) {
        deferred.then($.proxy(function () {
            this.updatePlayPauseButton();
            //If the playState is "playing" (==1), we are no longer waiting for the track to load
            if (playState === 1) loading = false;
          }), this);
      }, this));

      // Update the progress bar fill amount
      this.$api.on('positionChanged.rdio', $.proxy(function(e, position) {
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
	  this.$likeImg = this.$('#like'); //creates global var in initialized function
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
      $.post('/rate?from=rdio&id=' + currentRdioId + '&verdict=like', function () {
        alert('Like submitted! :D');
      });
    },
    dislike: function (e) {
      e.stopPropagation();
      e.preventDefault();
      $.post('/rate?from=rdio&id=' + currentRdioId + '&verdict=dislike', function () {
        alert('Dislike submitted! >:(');
      });
    },
    updatePlayPauseButton: function () {
      this.$playPauseButton.attr('src', playingImg[playing % 2]);//text(playingText[playing % 2]);
    }
  }))();
});
