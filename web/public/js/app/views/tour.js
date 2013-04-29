/*global define*/
define([
  'backbone',
  'joyride',
  'util/jqr!'
], function (bb) {
  //cookie to remember if the user has already taken the tour
  var cookieName = 'JoyRide';
  return new (bb.View.extend({
    el: '#joyRideTipContent',
    initialize: function () {
      if (!$.cookie(cookieName)) {
        this.$el.joyride({
          'tipLocation': 'bottom',         // 'top' or 'bottom' in relation to parent
          'nubPosition': 'auto',           // override on a per tooltip bases
          'scrollSpeed': 800,              // Page scrolling speed in ms
          'timer': 0,                   // 0 = off, all other numbers = time(ms)
          'startTimerOnClick': false,       // true/false to start timer on first click
          'nextButton': true,              // true/false for next button visibility
          'tipAnimation': 'fade',           // 'pop' or 'fade' in each tip
          'tipAnimationFadeSpeed': 500,    // if 'fade'- speed in ms of transition
          'cookieMonster': true,           // true/false for whether cookies are used
          'cookieName': cookieName,         // choose your own cookie name
          'cookieDomain': false,           // set to false or yoursite.com
          'tipContent': '#joyRideTipContent' // the ol element that lists the tips
        });
        this.$el.joyride();
      }
    }
  }))();
});
