/*global define*/
define(['backbone'], function (bb) {
  return bb.View.extend({
    initialize: function () {
      this.$el.addClass('loading-widget');
      // Add animated circles
      for (var i = 1; i <= 8; i++) {
        this.$el.append($('<div>').addClass('circle _' + i));
      }
    }
  });
});
