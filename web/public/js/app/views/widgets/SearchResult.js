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
      // If the type is 'r', the selection is an artist.
      // Since the artist key is not a 'playable' key, we need to use the topSongsKey to actually initiate playback for artists
      vent.trigger('play-key', (this.model.get('type') === 'r') ? this.model.get('topSongsKey') : this.model.get('key'));
    }
  });
});
