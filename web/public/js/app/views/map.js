/*global define*/
define([
  'backbone',
  'oms',
  'util/jqr!'
], function (bb) {
  // Maps API available as the variable `google`
  var contentMap = null;
  var iw = null;
  var markerArray = [];
  return new (bb.View.extend({
    el: '#map',
    initialize: function () {
      //this is centered on Coffeyville, KS - geographic center of US
      this.mapCenter = new google.maps.LatLng(39.8282, -98.5795);
        center: this.mapCenter,
        zoom: 4,
        mapTypeId: google.maps.MapTypeId.ROADMAP
      };
      this.map = new google.maps.Map(this.el, this.mapOptions);
      this.oms = new OverlappingMarkerSpiderfier(this.map, {keepSpiderfied: true});
      $.get('/rate?limit=10', $.proxy(function (data) {
        for (var position in data) {
          data[position].forEach($.proxy(function (d) {
            var split = position.split(",");
            var latLng = new google.maps.LatLng(split[0], split[1]);
            this.addNewMarker(latLng, d.title, d.artist, d.album, d.image);
          }, this));
        }
      }, this));
      iw = new google.maps.InfoWindow();
      this.oms.addListener('click', $.proxy(function(m) {
        iw.setContent(m.desc);
        iw.open(this.map, m);
      }, this));
      setTimeout($.proxy(function(){this.clearMarkers();}, this),5000);
    },

    /*TODO
      add listener to infowindow to listen for click on album art to play it
      implement google markerclusterer library
      set up vent listener for filter changes
    */

    addNewMarker: function (position, song, artist, album, image) {
      var marker = new google.maps.Marker({
        position: position,
        map: this.map,
        title: song,
        desc: '<span id="info-window"><img src="' + image + '" width=100 height=100>' +
                '<span><div id="iw-title">Song: ' + song + '</div>' +
                '<div id="iw-album">Album: ' + album + '</div>' +
                '<div id="iw-artist">Artist: ' + artist + '</div></span></span>'
      });
      this.oms.addMarker(marker);
      markerArray[markerArray.length] = marker;
    },

    clearMarkers: function() {
      if (iw) {
        iw.close();
      }
      for (var marker in markerArray) {
        marker.setMap(null);
      }
    }
  }))();
});
