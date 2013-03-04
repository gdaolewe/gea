/*global define*/
define(['backbone', 'util/jqr!'], function (bb) {
  return new (bb.View.extend({
    el: '#player',
    events: {
      'click #play-pause-img': 'togglePlay',
      'click #next': 'playNext',
      'click #previous': 'playPrevious'
    },
    initialize: function () {
      this.$playPauseButton = this.$('#play-pause-img');
    },
    togglePlay: function (e) {
      e.stopPropagation();
      e.preventDefault();
      var button = this.$playPauseButton;
      if(button.attr('src') == 'img/playbutton.jpg') {
        button.attr('src','img/pausebutton.jpg');
      } else {
        button.attr('src','img/playbutton.jpg');
      }
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
