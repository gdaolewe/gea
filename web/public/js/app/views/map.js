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
      this.mapCenter = new google.maps.LatLng(42.375200, -72.521200); //change to zoomed out version of all 50 states
      this.mapOptions = {
        center: this.mapCenter,
        zoom: 10,
        mapTypeId: google.maps.MapTypeId.ROADMAP
      };
      this.map = new google.maps.Map(this.el, this.mapOptions);
      this.oms = new OverlappingMarkerSpiderfier(this.map, {keepSpiderfied: true});
      
// also want to figure out how to get album art into the pins to make custom ones

      $.get('/rate?limit=10', $.proxy(function (data) {
        for (var position in data) {
          data[position].forEach($.proxy(function (d) {
            var split = position.split(",");
            var latLng = new google.maps.LatLng(split[0], split[1]);
            this.addNewMarker(latLng, d.title, d.artist, d.album);
          }, this));
        }
      }, this));
            /*sets up listeners*/
      iw = new google.maps.InfoWindow();
      this.oms.addListener('click', $.proxy(function(m) {
        iw.setContent(m.desc);
        iw.open(this.map, m);
      }, this));
      setTimeout($.proxy(function(){this.clearMarkers();}, this),5000);
    },
//maybe add listener to infowindow to listen for click on album art to play it
    addNewMarker: function (position, song, artist, album) {
      var marker = new google.maps.Marker({
        position: position,
        map: this.map,
        title: song,
        //animation: google.maps.Animation.DROP, //removed for easier testing
        desc: '<span id="info-window"><img src="http://cdn3.rd.io/album/3/3/f/0000000000029f33/square-200.jpg" width=100 height=100>'+
                '<span><div id="iw-title">Song: ' + song + '</div>'+
                '<div id="iw-album">Album: ' + album + '</div>'+
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
