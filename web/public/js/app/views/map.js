/*global define*/
define([
  'backbone',
  'oms',
  'util/jqr!'
], function (bb) {
  // Maps API available as the variable `google`
  var marker = null;
  return new (bb.View.extend({
    el: '#map',
    initialize: function () {
      //both pins and mapCenter are currently set to Amherst, MA
      this.markerLocation = new google.maps.LatLng(42.375200, -72.521200);
      this.mapCenter = new google.maps.LatLng(42.375200, -72.521200);
      this.mapOptions = {
        center: this.mapCenter,
        zoom: 10,
        mapTypeId: google.maps.MapTypeId.ROADMAP
      };
      this.map = new google.maps.Map(this.el, this.mapOptions);
      this.oms = new OverlappingMarkerSpiderfier(this.map);
      /*sets up listeners
      
      var iw = new google.maps.InfoWindow();
      this.oms.addListener('click', function(marker) {
        iw.setContent(marker.desc);
        iw.open(this.map, this.marker);
      });*/
      setTimeout($.proxy(function () {
        this.addAllMarkers();
      }, this), 200);
    },

    addNewMarker: function () {
      marker = new google.maps.Marker({
        position: this.markerLocation,
        map: this.map,
        title:"Hello World!",
        animation: google.maps.Animation.DROP
      });
      this.oms.addMarker(marker);
    },

    addAllMarkers: function () {
      for( var i = 0; i <= 10; i++){
        setTimeout($.proxy(function () {
          this.addNewMarker();
        }, this), i*200);
      }
    }
  }))();
});
