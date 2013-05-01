/*global define*/
define([
  'backbone',
  'util/constants',
  './widgets/Loading',
  'app/vent',
  'util/jqr!'
], function (
  bb,
  constants,
  LoadingWidget,
  vent
) {

  //Timeout thread for auto-search
  var thread = null;

  return new (bb.View.extend({
    el: '#map-panel',
    events: {
      'change #map-filter': 'change'
    },
    initialize: function () {
      this.$select = this.$('#map-filter');
      this.$loading = $('#map-loading');
      // Listen for a pinLoaded event to disable the loading widget
      vent.on('pinsLoaded', $.proxy(function () {
        this.removeLoadingWidget();
      }, this));
      this.createLoadingWidget();
      //this.filterMap();
    },
    change: function (e) {
      e.preventDefault();
      e.stopPropagation();
      this.filterMap();
    },
    filterMap: function () {
      // Get the filter map selection value
      var val = this.$select.val();
      // Remove the loading widget from any previous request
      this.removeLoadingWidget();
      // Create and add a loading widget while the data loads
      this.createLoadingWidget();
      // Trigger an event to load the appropriate data
      vent.trigger('mapFilter', val);
    },
    createLoadingWidget: function () {
      // Initialize the loading widget
      this.loadingWidget = new LoadingWidget();
      // Append the loading widget to the page
      this.$loading.append(this.loadingWidget.el);
    },
    removeLoadingWidget: function () {
      if (this.loadingWidget) {
        // Remove loading indicator
        this.loadingWidget.remove();
        delete this.loadingWidget;
      }
    }
  }))();
});
