/*global define*/
define([
  'backbone',
  'joyride',
  'util/jqr!'
], function (bb) {
  return new (bb.View.extend({
    el: '#joyRideTipContent',
    initialize: function () {
      this.$el.joyride({
        'startOffset':2,
        'tipLocation': 'bottom',         // 'top' or 'bottom' in relation to parent
        'nubPosition': 'auto',           // override on a per tooltip bases
        'scrollSpeed': 800,              // Page scrolling speed in ms
        'timer': 0,                   // 0 = off, all other numbers = time(ms)
        'startTimerOnClick': false,       // true/false to start timer on first click
        'nextButton': true,              // true/false for next button visibility
        'tipAnimation': 'fade',           // 'pop' or 'fade' in each tip
        'pauseAfter': [],                // array of indexes where to pause the tour after
        'tipAnimationFadeSpeed': 500,    // if 'fade'- speed in ms of transition
        'cookieMonster': false,           // true/false for whether cookies are used
        'cookieName': 'JoyRide',         // choose your own cookie name
        'cookieDomain': false,           // set to false or yoursite.com
        'tipContent': '#joyRideTipContent'
        //'tipContainer': body,            // Where the tip be attached if not inline
        //'postRideCallback': $noop,       // a method to call once the tour closes
        //'postStepCallback': $noop        // A method to call after each step
      });
      this.$el.joyride();
    }
  }))();
});
