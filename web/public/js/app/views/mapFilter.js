/*global define*/
define([
  'backbone',
  'util/constants',
  'app/vent',
  'util/jqr!'
], function (
  bb,
  constants,
  vent
) {

  //Timeout thread for auto-search
  var thread = null;

  return new (bb.View.extend({
    el: '#map-panel',
    events: {
      'keyup #map-filter': 'keyup',
      'change #map-filter': 'change'
    },
    initialize: function () {
      this.$select = this.$('#map-filter');
    },
    keyup: function (e) {
      e.preventDefault();
      e.stopPropagation();
      this.filterMap();
    },
    change: function (e) {
      e.preventDefault();
      e.stopPropagation();
      this.filterMap();
    },
    filterMap: function () {
      var val = this.$select.val();
      vent.trigger('mapFilter', val);
    }
  }))();
});
