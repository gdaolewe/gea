/*global define*/
define(['backbone', 'text!./SearchResult.html'], function (bb, html) {
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
      // Artist results don't have an artist field (ironically)
      if (this.$el.hasClass('r')) {
        this.$('.artist').remove();
      }
    },
    click: function () {
      console.log(this.model);
    }
  });
});
