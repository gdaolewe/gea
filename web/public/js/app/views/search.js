/*global define*/
define([
  'backbone',
  'util/constants',
  '../data/searchResults',
  './widgets/Loading',
  './widgets/SearchResult',
  './player',
  'app/vent',
  'util/jqr!'
], function (
  bb,
  constants,
  searchResults,
  LoadingWidget,
  SearchResult,
  player,
  vent
) {
  // Store ./widgets/SearchResult views
  var resultViews = [];

  // Reference sidebar element
  var $right = $('#right');

  //Timeout thread for auto-search
  var thread = null;

  //Boolean to indicate that search should *not* auto-execute
  var focused = false;

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
      // Listen for the keyboard shortcut to focus the search input element
      vent.on('focusSearch-shortcut', $.proxy(function () {
        // 'keyup' catches the release from the 'enter' button, so
        // prevent it from using that input as a reason to execute a search
        focused = true;
        this.$input.focus();
      }, this));
    },
    keyup: function (e) {
      e.preventDefault();
      e.stopPropagation();
      if (focused) {
        focused = false;
        return;
      }
      if (e.keyCode === constants.KEY_ENTER) {
        clearTimeout(thread);
        this.executeSearch();
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
      thread = setTimeout($.proxy(function () { this.executeSearch(); }, this), 500);
    },
    executeSearch: function () {
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
