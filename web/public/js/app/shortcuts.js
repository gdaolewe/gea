/*global define*/
define([
  'backbone',
  'app/vent',
  'backboneshortcuts',
  'util/jqr!'
], function (bb, vent) {
  return new (bb.Shortcuts.extend({
    // List of shortcut keys and their associated functions
    // Each vent.trigger fires an event to whatever is listening for that particular event
    shortcuts: {
      "space": "playPause",
      "right": "next",
      "left": "previous",
      "enter": "focusSearch"
    },
    playPause: function () {
      vent.trigger('playPause-shortcut');
    },
    next: function () {
      vent.trigger('next-shortcut');
    },
    previous: function () {
      vent.trigger('previous-shortcut');
    },
    focusSearch: function () {
      vent.trigger('focusSearch-shortcut');
    }
  }));
});
