/*global define*/
define(['backbone'], function (bb) {
  // View constructor
  // These views represent a search result
  return bb.View.extend({
    initialize: function () {
      this.render();
    },
    render: function () {
      this.$el.text(this.model.get('name'));
    }
  });
});
