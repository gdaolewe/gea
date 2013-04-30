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

  //Truncating length
  var maxLength = 25;

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
      var nameTruncated = this.model.get('name');
      nameTruncated = (nameTruncated && nameTruncated.length > maxLength) ?
                          $.trim(nameTruncated).substring(0, maxLength).trim(this) + "..."
                          : nameTruncated;
      var artistTruncated = this.model.get('artist');
      artistTruncated = (artistTruncated && artistTruncated.length > maxLength) ?
                          "by " + $.trim().substring(0, maxLength).trim(this) + "..."
                          : (artistTruncated) ? "by " + artistTruncated : "";
      this.$el.html(template({
        icon: this.model.get('icon'),
        artist: artistTruncated,
        key: this.model.get('key'),
        name: nameTruncated
      }));

      if (this.$el.hasClass('r')) {
        // Artist results don't have an artist field (ironically)
        this.$('.artist').remove();
        // Use the artist-icon to show this is an artist result, not a track result
        this.$('.track-icon').toggleClass('track-icon artist-icon');
      } else if (this.$el.hasClass('a')) {
        // Use the album-icon to show this is an album result, not a track result
        this.$('.track-icon').toggleClass('track-icon album-icon');
      }
    },
    click: function () {
      // If the type is 'r', the selection is an artist.
      // Since the artist key is not a 'playable' key, we need to use the topSongsKey to actually initiate playback for artists
      vent.trigger('play-key', (this.model.get('type') === 'r') ? this.model.get('topSongsKey') : this.model.get('key'));
    }
  });
});
