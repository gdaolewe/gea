/*global define*/
define(['backbone', 'util/jqr!'], function (bb) {
  var playing = 1;
  var playingText = ['Play', 'Pause'];

  return new (bb.View.extend({
    el: '#player',
    events: {
      'click #play-pause': 'togglePlay',
      'click #next': 'playNext',
      'click #previous': 'playPrevious'
    },
    initialize: function () {
      this.$playPauseButton = this.$('#play-pause');
      this.$playPauseButton.text(playingText[playing]);
      this.$progressBarFill = this.$('#fill');
      this.$('#player-art').text('Art loaded.');
      this.$('#player-metadata').text('Metadata loaded.');
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
      console.log('Next song!');
    },
    playPrevious: function (e) {
      e.stopPropagation();
      e.preventDefault();
      console.log('Previous song!');
    }
  }))();
});
