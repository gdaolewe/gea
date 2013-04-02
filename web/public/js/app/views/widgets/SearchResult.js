/*global define*/
define([
  'backbone',
  'text!./SearchResult.html',
  'app/vent'
], function (
  bb,
  html,
  vent
) {
  /* This is the View aspect of the Model-View-Collection design pattern.
   * When the collection fires the event to notify that it is has changed,
   * this view updates the elements appropriately.
  */
  // Template
  var template = _.template(html);

  // View constructor
  // These views represent a search result
  return bb.View.extend({
    events: {
      'click': 'click'
    },
    initialize: function () {
      this.$el.addClass('result').addClass(this.model.get('key')[0]);
      this.render();
    },
    render: function () {
      this.$el.html(template({
        icon: this.model.get('icon'),
        artist: this.model.get('artist'),
        key: this.model.get('key'),
        name: this.model.get('name')
      }));

      if (this.$el.hasClass('r')) {
        // Artist results don't have an artist field (ironically)
        this.$('.artist').remove();
      } else if (this.$el.hasClass('a')) {
        // Remove album from album results
        this.$('.album').remove()
      }
    },
    click: function () {
      vent.trigger('play-key', this.model.get('key'));
    }
  });
});
