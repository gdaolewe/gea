/*global define*/
define([
  'backbone',
  'util/constants',
  'util/jqr!'
], function (
  bb,
  constants,
  vent
) {

  //Timeout thread for auto-search
  var thread = null;

  return new (bb.View.extend({
    el: '#map-panel',
    events: {
      'keyup #map-filter': 'keyup',
    },
    initialize: function () {
      this.$input = this.$('#map-filter');
    },
    keyup: function (e) {
      e.preventDefault();
      e.stopPropagation();
      if (e.keyCode === constants.KEY_ENTER) {
        clearTimeout(thread);
        this.filterMap();
        return;
      }
      $.each(constants, $.proxy(function (key, val) {
        if (val === e.keyCode) {
          e.found = true;
          return;
        }
      }, e));
      if (e.found) return;
      clearTimeout(thread);
      thread = setTimeout($.proxy(function () { this.filterMap(); }, this), 500);
    },
    filterMap: function () {
      var val = this.$input.val();
      if (val.length > 1 && val[0].match(/[\w\d]/)) {
        //Abort any current request
        if (this.$req) this.$req.abort();
        this.loadingWidget = new LoadingWidget();
        this.emptyResults();
        this.$results.append(this.loadingWidget.el);
        // Perform search
        this.$req = $.get('/rdio/search?query=' + val).done(function (data) {
          searchResults.reset(data.result.results);
        }).fail(function () {
          searchResults.reset();
        });
      }
    },
    topTrendingClick: function (e) {
      e.stopPropagation();
      e.preventDefault();
      $.get('/rate?limit=10', function (data) {
        var alertText = '';
        for (var state in data) {
          alertText += state + '\n';
          var counter = 1;
          data[state].forEach(function (d) {
            var result = '';
            if (d.title) result += '\'' + d.title + '\'';
            if (d.artist) result += ' by ' + d.artist;
            if (d.album) result += ' from \'' + d.album + '\'';
            if (result) {
              alertText += counter + '. ' + result + '\n';
              counter++;
            }
          });
          alertText += '\n';
        }
        alert(alertText.substr(0, alertText.length - 2));
      });
    },
    render: function () {
      // Remove loading indicator
      this.loadingWidget.remove();
      delete this.loadingWidget;
      // Create new view for each search result
      searchResults.forEach($.proxy(function (r) {
        var view = new SearchResult({model: r});
        this.$results.append(view.el);
        resultViews.push(view);
      }, this));
    },
    emptyResults: function () {
      // Remove each previous search result
      resultViews.forEach(function (v) {
        v.remove();
      });
      // Reset resultViews array
      resultViews = [];
    },
    // Set the height of the results
    resizeResults: function () {
      // New height is height of the right bar minus the player and input heights
      this.$results.height($right.height() - player.$el.height() - this.$input.height() - this.$topTrending.height());
    }
  }))();
});
