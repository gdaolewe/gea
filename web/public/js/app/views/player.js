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
  var playing = false;
  var lastTrack = false;
  var song = null;
  var playingClass = ['play-button', 'pause-button'];
  var loading = false;
  var dragging = false;
  var deferred = new promise.Promise();
  var streamerPromise = new promise.Promise();
  var currentStreamId = null;
  var likeEnabled = true;
  var dislikeEnabled = true;

  return new (bb.View.extend({
    el: '#player',
    events: {
      'click #play-pause': 'togglePlay',
      'click #next': 'playNext',
      'click #previous': 'playPrevious',
      'click #like': 'like',
      'click #dislike': 'dislike',
      'mousedown #progress-bar': 'drag',
      'mouseup #progress-bar': 'undrag',
      'mousemove #progress-bar': 'seek'
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
      this.sourceLength = 0;
      // Rdio streamer object
      this.$streamer = rdio;
      // Queue the first album when ready
      streamerPromise.then($.proxy(function () {
        this.$streamer.bind('ready', $.proxy(function() {
          // Listen for new songs to play
          vent.on('play-key', $.proxy(function (key) {
            currentStreamId = key;
            this.$streamer.play(key);
          }, this));

          // Listen for the keyboard shortcuts
          vent.on('playPause-shortcut', $.proxy(function () {
            this.togglePlay($.Event());
          }, this));
          vent.on('next-shortcut', $.proxy(function () {
            this.playNext($.Event());
          }, this));
          vent.on('previous-shortcut', $.proxy(function () {
            this.playPrevious($.Event());
          }, this));
          vent.on('like-shortcut', $.proxy(function () {
            this.like($.Event());
          }, this));
          vent.on('dislike-shortcut', $.proxy(function () {
            this.dislike($.Event());
          }, this));
        }, this));
      }, this));

      // When the playing track has changed, adjust the album art, track, album, and artist info accordingly
      streamerPromise.then($.proxy(function () {
        this.$streamer.bind('playingTrackChanged', $.proxy(function(e, playingTrack, sourcePosition) {
          deferred.done();
          if (playingTrack) {
            //Get the current stream key
            currentStreamId = playingTrack.key;
            //Store which track this is in the current playing source
            this.sourcePosition = sourcePosition;
            //Ensure the like/dislike buttons are enabled
            this.enableSocialButtons();
            //Set global boolean to indicate if this is the last track in the source or not
            lastTrack = (this.sourcePosition === this.sourceLength - 1);
            //If this is the last track, switch to the 'disabled' button
            (lastTrack) ? this.$nextButton.addClass('end') : this.$nextButton.removeClass('end');
            //Update the play/pause button correctly
            deferred.then($.proxy(function () {
              this.updatePlayPauseButton();
            }), this);
            // Save the current track's duration for computing the percent remaining
            this.duration = playingTrack.duration;
            // Update the album art div to reflect the current track's art
            this.$art.css('background-image', "url(" + playingTrack.icon + ")");
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

      streamerPromise.then($.proxy(function () {
        this.$streamer.bind('playingSourceChanged', $.proxy(function(e, playingSource) {
          this.sourceLength = playingSource.length;
        }, this));
      }, this));

      // When the play-state has changed, adjust the play/pause button appropriately
      streamerPromise.then($.proxy(function () {
        this.$streamer.bind('playStateChanged', $.proxy(function(e, playState) {
          deferred.then($.proxy(function () {
              //If the playState is "playing" (==1), we are no longer waiting for the track to load
              // else, if playState is *not* "buffering" (!=3), the player is paused/not playing
              if (playState === 1) {
                loading = false;
                playing = true;
              } else if (playState != 3) playing = false;
              this.updatePlayPauseButton();
            }), this);
        }, this));
      }, this));

      // Update the progress bar fill amount
      streamerPromise.then($.proxy(function () {
        this.$streamer.bind('positionChanged', $.proxy(function(e, position) {
          /* When the streaming service calls the positionChanged callback function,
             adjust the width of the progress bar's "fill" to
             scale according the percent played of the track's duration
          */
          this.$progressBarFill.css('width', Math.floor(100*position/this.duration)+'%');
          this.trackPosition = position;
        }, this));
      }, this));
      $.get('/' + this.$streamer.name + '/getPlaybackToken', $.proxy(function (data) {
        this.$streamer.setup(this.$('#api'), data.result);
        streamerPromise.done();
      }, this));
      this.$playPauseButton = this.$('#play-pause');
      this.$nextButton = this.$('#next');
      this.$progressBar = this.$('#progress-bar');
      this.$progressBarFill = this.$('#fill');
	    this.$likeImg = this.$('#like'); //creates global var in initialized function
      this.$dislikeImg = this.$('#dislike');
    },
    togglePlay: function (e) {
      e.stopPropagation();
      e.preventDefault();
      deferred.then($.proxy(function () {
        //If we're still waiting to load from the streaming service, don't handle this.
        if (loading) return;
        playing ? this.$streamer.pause() : this.$streamer.play();
      }, this));
    },
    playNext: function (e) {
      e.stopPropagation();
      e.preventDefault();
      deferred.then($.proxy(function () {
        //If we're still waiting to load from the streaming service, don't handle this.
        if (loading) return;
        if (lastTrack) return;
        this.loading();
        this.$streamer.next();
      }, this));
    },
    playPrevious: function (e) {
      e.stopPropagation();
      e.preventDefault();
      deferred.then($.proxy(function () {
        //If we're still waiting to load from the streaming service, don't handle this.
        if (loading) return;
        //If we've played more than 5 seconds (or this is the first track)
        // manually seek to the beginning of the current track instead of changing to the previous track
        if (this.trackPosition >= 5 || this.sourcePosition === 0) {
          this.$streamer.seek(0);
        } else {
          //Otherwise, seek to the beginning of this track and then change to the previous (ensures the right behavior)
          this.$streamer.seek(0);
          this.loading();
          this.$streamer.previous();
        }
      }, this));
    },
    like: function (e) {
      e.stopPropagation();
      e.preventDefault();
      if (!loading && currentStreamId && likeEnabled) {
        if (this.$likeReq) this.$likeReq.abort();
        this.$likeReq = $.post('/rate?from=' + this.$streamer.name + '&id=' + currentStreamId + '&verdict=like', $.proxy(function () {
          this.$likeImg.toggleClass('disabled');
          likeEnabled = false;
          if (!dislikeEnabled) {
            this.$dislikeImg.toggleClass('disabled');
            dislikeEnabled = true;
          }
          alert('Like submitted! :D');
        }, this));
      }
    },
    dislike: function (e) {
      e.stopPropagation();
      e.preventDefault();
      if (!loading && currentStreamId && dislikeEnabled) {
        if (this.$dislikeReq) this.$dislikeReq.abort();
        this.$dislikeReq = $.post('/rate?from=' + this.$streamer.name + '&id=' + currentStreamId + '&verdict=dislike', $.proxy(function () {
          if (!likeEnabled) {
            this.$likeImg.toggleClass('disabled');
            likeEnabled = true;
          }
          this.$dislikeImg.toggleClass('disabled');
          dislikeEnabled = false;
          alert('Dislike submitted! >:(');
        }, this));
      }
    },
    drag: function (e) {
      e.stopPropagation();
      e.preventDefault();
      // User has begun seeking by pressing the mouse button down on the progress bar
      dragging = true;
      // Update the progress bar position (i.e. seek)
      this.updateProgressBar(e.pageX);
    },
    undrag: function (e) {
      e.stopPropagation();
      e.preventDefault();
      if (dragging) {
        // User has completed seeking by releasing the mouse button
        dragging = false;
        // Update the progress bar position (i.e. seek)
        this.updateProgressBar(e.pageX);
      }
    },
    seek: function (e) {
      e.stopPropagation();
      e.preventDefault();
      if (dragging) {
        // If the user is in the process of dragging (i.e. seeking), update the progress bar position
        this.updateProgressBar(e.pageX);
      }
    },
    updateProgressBar: function (x) {
      // Get the position of the click in the progress bar
      var clickPosition = x - this.$progressBar.offset().left;
      // Turn it into a percentage (0 to 1)
      var percentage = clickPosition / this.$progressBar.width();
      // Ensure percentage is at most 1 (=100%)
      percentage = (percentage > 1) ? 1 : percentage;
      // Ensure percentage is at least 0 (=0%)
      percentage = (percentage < 0) ? 0 : percentage;
      // Get the 'seconds' into the song that the percentage translates to
      var seconds = percentage * this.duration;
      // Tell the streaming service to seek to that position
      this.$streamer.seek(seconds);
    },
    updatePlayPauseButton: function () {
      this.$playPauseButton.attr('class', playingClass[playing ? 1 : 0]);
    },
    enableSocialButtons: function () {
      if (!likeEnabled) {
        this.$likeImg.toggleClass('disabled');
        likeEnabled = true;
      }
      if (!dislikeEnabled) {
        this.$dislikeImg.toggleClass('disabled');
        dislikeEnabled = true;
      }
    },
    loading: function () {
      loading = true;
      currentStreamId = null;
    }
  }))();
});
