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
  var deferred = new $.Deferred();

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
          console.log("playing: "+playing);
          deferred.then($.proxy(function () {
            this.updatePlayPauseButton();
          }), this);
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
        loading = false;
      }, this));
      
      // When the play-state has changed, adjust the play/pause button appropriately
      this.$api.bind('playStateChanged.rdio', $.proxy(function(e, playState) {
        deferred.then($.proxy(function () {
            this.updatePlayPauseButton();
          }), this);
      }, this));
      
      // Update the progress bar fill amount
      this.$api.bind('positionChanged.rdio', $.proxy(function(e, position) {
        // When Rdio calls the positionChanged callback function, adjust the width of the progress bar's "fill" to 
        // scale according the percent played of the track's duration
        this.$progressBarFill.css('width', Math.floor(100*position/this.duration)+'%');
        this.trackPosition = position;
        console.log(position + ', loading: ' + loading);
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
        console.log("Play/pause toggle.");
        //if (loading) return;
        ++playing;
        playing % 2 ? this.$api.rdio().play() : this.$api.rdio().pause();
      }, this));
    },
    playNext: function (e) {
      e.stopPropagation();
      e.preventDefault();
      deferred.then($.proxy(function () { 
        //if (loading) return;
        playing = 1;
        this.$api.rdio().next();
        //loading = true;
      }, this));
    },
    playPrevious: function (e) {
      e.stopPropagation();
      e.preventDefault();
      deferred.then($.proxy(function () { 
        //if (loading) return;
        playing = (this.sourcePosition === 0) ? 0 : 1;
        this.$api.rdio().previous();
        if (this.trackPosition === 0) {
          console.log("Loading previous song. " + this.trackPosition);
          //loading = true;
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
    render: function () {
      /*$.when($.get('/artist/' + song.get('artist')), $.get('/album/' + song.get('album'))).done($.proxy(function (artist, album) {
        this.$metadata.text(album[0].title + ' - ' + song.get('title') + ' by ' + artist[0].name);
      }, this));*/
    },
    updatePlayPauseButton: function () {
      this.$playPauseButton.text(playingText[playing % 2]);
    }
  }))();
});
