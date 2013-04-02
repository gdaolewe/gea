/*global define*/
define([
  'backbone',
  'promise',
  'app/vent',
  '../streamers/rdio',
  'jqueryrdio',
  'util/jqr!'
], function (
  bb,
  promise,
  vent,
  rdio
) {
  var playing = 0;
  var song = null;
  var playingClass = 'play-button pause-button';
  var loading = false;
  var deferred = new promise.Promise();
  var streamerPromise = new promise.Promise();
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
      // Rdio streamer object
      this.$streamer = rdio;
      // Queue the first album when ready
      streamerPromise.then($.proxy(function () {
        this.$streamer.bind('ready.rdio', $.proxy(function() {
          this.$streamer.queue(currentRdioId);
          // Listen for new songs to play
          vent.on('play-key', $.proxy(function (key) {
            currentRdioId = key;
            this.$streamer.play(key);
          }, this));
        }, this));
      }, this));

      // When the playing track has changed, adjust the album art, track, album, and artist info accordingly
      streamerPromise.then($.proxy(function () {
        this.$streamer.bind('playingTrackChanged.rdio', $.proxy(function(e, playingTrack, sourcePosition) {
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
      }, this));

      // When the play-state has changed, adjust the play/pause button appropriately
      streamerPromise.then($.proxy(function () {
        this.$streamer.bind('playStateChanged.rdio', $.proxy(function(e, playState) {
          deferred.then($.proxy(function () {
              this.updatePlayPauseButton();
              //If the playState is "playing" (==1), we are no longer waiting for the track to load
              if (playState === 1) loading = false;
            }), this);
        }, this));
      }, this));

      // Update the progress bar fill amount
      streamerPromise.then($.proxy(function () {
        this.$streamer.bind('positionChanged.rdio', $.proxy(function(e, position) {
          // When Rdio calls the positionChanged callback function, adjust the width of the progress bar's "fill" to
          // scale according the percent played of the track's duration
          this.$progressBarFill.css('width', Math.floor(100*position/this.duration)+'%');
          this.trackPosition = position;
        }, this));
      }, this));
      $.get('/rdio/getPlaybackToken', $.proxy(function (data) {
        this.$streamer.setup(this.$('#api'), data.result);
        streamerPromise.done();
      }, this));
      this.$playPauseButton = this.$('#play-pause');
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
        playing % 2 ? this.$streamer.play() : this.$streamer.pause();
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
        this.$streamer.next();
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
          this.$streamer.seek(0);
        } else {
          //Otherwise, seek to the beginning of this track and then change to the previous (ensures the right behavior)
          this.$streamer.seek(0);
          loading = true;
          this.$streamer.previous();
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
      this.$playPauseButton.toggleClass(playingClass);
    }
  }))();
});
