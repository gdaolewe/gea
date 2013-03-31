/*global define*/
define([
  'backbone',
  'util/constants',
  '../data/searchResults',
  './widgets/Loading',
  './widgets/SearchResult',
  'util/jqr!'
], function (
  bb,
  constants,
  searchResults,
  LoadingWidget,
  SearchResult
) {
  // Store ./widgets/SearchResult views
  var resultViews = [];
  return new (bb.View.extend({
    el: '#search',
    events: {
      'keyup #search-input': 'keyup'
    },
    initialize: function () {
      this.$input = this.$('#search-input');
      this.$results = this.$('#search-results');
      this.listenTo(searchResults, 'reset', this.render);
    },
    keyup: function (e) {
      if (e.keyCode !== constants.KEY_ENTER) {
        return;
      }
      e.preventDefault();
      e.stopPropagation();
      this.loadingWidget = new LoadingWidget();
      this.$results.append(this.loadingWidget.el);
      var val = this.$input.val();
      // Perform search
      $.get('/rdio/search?query=' + val, function (data) {
        searchResults.reset(data.result.results);
      });
    },
    render: function () {
      // Remove each previous search result
      resultViews.forEach(function (v) {
        v.remove();
      });
      // Reset resultViews array
      resultViews = [];
      // Remove loading indicator
      this.loadingWidget.remove();
      delete this.loadingWidget;
      // Create new view for each search result
      searchResults.forEach($.proxy(function (r) {
        var view = new SearchResult({model: r});
        this.$results.append(view.el);
        resultViews.push(view);
      }, this));
    }
  }))();
});
