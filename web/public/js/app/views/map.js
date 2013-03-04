/*global define*/
define(['backbone', 'util/jqr!'], function (bb) {
  return new (bb.View.extend({
    el: '#map',
    initialize: function () {
      this.$el.text('Map loaded.');
    }
  }))();
});
