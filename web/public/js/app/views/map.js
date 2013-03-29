/*global define*/
define([
  'backbone',
  'async!//maps.googleapis.com/maps/api/js?key=AIzaSyB6n1ohXSe-LZSKYD730M9ZWBPI5Z8nTJ4&sensor=false',
  'util/jqr!'
], function (bb) {
  // Maps API available as the variable `google`
  return new (bb.View.extend({
    el: '#map',
    initialize: function () {
      this.$el.text('Map loaded.');
    }
  }))();
});
