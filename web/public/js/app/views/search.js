/*global define*/
define([
  'backbone',
  'util/constants',
  '../data/searchResults',
  './widgets/Loading',
  './widgets/SearchResult',
  './player',
  'util/jqr!'
], function (
  bb,
  constants,
  searchResults,
  LoadingWidget,
  SearchResult,
  player
) {
  // Store ./widgets/SearchResult views
  var resultViews = [];

  // Reference sidebar element
  var $right = $('#right');

  return new (bb.View.extend({
    el: '#search',
    events: {
      'keyup #search-input': 'keyup'
    },
    initialize: function () {
      this.$input = this.$('#search-input');
      this.$results = this.$('#search-results');
      this.listenTo(searchResults, 'reset', this.render);
      // Getting overflow to work correctly with 100% height is tricky
      // Here, we dynamically resize the results div as needed
      this.resizeResults();
      player.$el.on('reflow', $.proxy(this.resizeResults, this));
      $(window).on('resize', $.proxy(this.resizeResults, this));
    },
    keyup: function (e) {
      if (e.keyCode !== constants.KEY_ENTER) {
        return;
      }
      e.preventDefault();
      e.stopPropagation();
      this.loadingWidget = new LoadingWidget();
      this.emptyResults();
      this.$results.append(this.loadingWidget.el);
      var val = this.$input.val();
      // Perform search
      $.get('/rdio/search?query=' + val, function (data) {
        searchResults.reset(data.result.results);
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
      this.$results.height($right.height() - player.$el.height() - this.$input.height());
    }
  }))();
});
