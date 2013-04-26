/*global define*/
define([
  'backbone',
  'oms',
  'util/jqr!'
], function (bb) {
  // Maps API available as the variable `google`
  var contentMap = null;
  var iw = null;
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
      this.oms = new OverlappingMarkerSpiderfier(this.map, {keepSpiderfied: true});
      
// this is the code for grabbing the top 10 from the server
// need to figure out how to get this information into the pin information
// also want to figure out how to get album art into the pins to make custom ones
// be adding the pins as we go with this information so won't need the marker functions anymore
        /*$.get('/rate?limit=10', function (data) {
        var alertText = '';
        var counter = 1;
        data.forEach($.proxy(function (d) {
          var result = '';
          if (d.title) result += '\'' + d.title + '\'';
          if (d.artist) result += ' by ' + d.artist;
          if (d.album) result += ' from \'' + d.album + '\'';
          if (result) {
            alertText += counter + '. ' + result + '\n';
            counter++;
          }
        }, this));*/
      setTimeout($.proxy(function () {
        this.addAllMarkers();
      }, this), 200);
            /*sets up listeners*/
      iw = new google.maps.InfoWindow();
      this.oms.addListener('click', $.proxy(function(m) {
        iw.setContent(m.desc);
        iw.open(this.map, m);
      }, this));

    },
//maybe add listener to infowindow to listen for click on album art to play it
    addNewMarker: function () {
      var marker = new google.maps.Marker({
        position: this.markerLocation,
        map: this.map,
        title:"Hello World!",
        //animation: google.maps.Animation.DROP, //removed for easier testing
        desc: '<span id="info-window"><img src="http://cdn3.rd.io/album/3/3/f/0000000000029f33/square-200.jpg" width=100 height=100>'+
                '<span><div id="iw-title">Song: Planet Telex</div>'+
                '<div id="iw-album">Album: The Bends</div>'+
                '<div id="iw-artist">Artist: Radiohead</div></span></span>'
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
