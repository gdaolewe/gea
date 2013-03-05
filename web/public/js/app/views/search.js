/*global define*/
define(['backbone', 'util/jqr!'], function (bb) {
  return new (bb.View.extend({
    el: '#results',
    initialize: function () {
      this.$el.text('Search loaded.');
    }
  }))();
});
