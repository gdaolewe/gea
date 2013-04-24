/*global define*/
define([
  'backbone',
  'jqueryjoyride',
  'util/jqr!'
], function (
  bb
) {
  return new (bb.View.extend({
    el: '#joyRideTipContent',
    initialize: function () {
      console.log(this.$el.joyride);
      this.$el.joyride({
        'autoStart': true,
        'tipLocation': 'bottom',         // 'top' or 'bottom' in relation to parent
        'nubPosition': 'auto',           // override on a per tooltip bases
        'scrollSpeed': 300,              // Page scrolling speed in ms
        'timer': 2000,                   // 0 = off, all other numbers = time(ms)
        'startTimerOnClick': true,       // true/false to start timer on first click
        'nextButton': true,              // true/false for next button visibility
        'tipAnimation': 'pop',           // 'pop' or 'fade' in each tip
        'pauseAfter': [],                // array of indexes where to pause the tour after
        'tipAnimationFadeSpeed': 300,    // if 'fade'- speed in ms of transition
        'cookieMonster': true,           // true/false for whether cookies are used
        'cookieName': 'JoyRide',         // choose your own cookie name
        'cookieDomain': false,           // set to false or yoursite.com
        'tipContent': '#joyRideTipContent'
        //'tipContainer': body,            // Where the tip be attached if not inline
        //'postRideCallback': $noop,       // a method to call once the tour closes
        //'postStepCallback': $noop        // A method to call after each step
      });
    }
  }))();
});
