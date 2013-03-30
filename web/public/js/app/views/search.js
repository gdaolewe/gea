/*global define*/
define([
  'backbone',
  'util/constants',
  './widgets/Loading',
  'util/jqr!'
], function (
  bb,
  constants,
  LoadingWidget
) {
  return new (bb.View.extend({
    el: '#search',
    events: {
      'keyup #search-input': 'keyup'
    },
    initialize: function () {
      this.$input = this.$('#search-input');
    },
    keyup: function (e) {
      if (e.keyCode === constants.KEY_ENTER) {
        e.preventDefault();
        e.stopPropagation();
        this.$el.append(new LoadingWidget().$el);
      }
    }
  }))();
});
