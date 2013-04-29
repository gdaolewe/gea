/*global define*/
define([
  'backbone',
  'text!./mapMarker.html',
  'app/vent',
  'oms',
  'mc',
  'util/jqr!'
], function (bb, html, vent) {
  // Template
  var template = _.template(html);

  // Maps API available as the variable `google`
  var contentMap = null;
  var iw = null;
  var markerArray = [];
  //var newImage = http://maps.google.com/mapfiles/ms/icons/blue-dot.png;

  return new (bb.View.extend({
    el: '#map',
    events: {
      'click #iw-img': 'playSong'
    },
    initialize: function () {
      //this is centered on Coffeyville, KS - geographic center of US
      this.mapCenter = new google.maps.LatLng(39.8282, -98.5795);
      this.mapOptions = {
        center: this.mapCenter,
        zoom: 4,
        mapTypeId: google.maps.MapTypeId.ROADMAP
      };
      //creating a GoogleMap object
      this.map = new google.maps.Map(this.el, this.mapOptions);
      //creating an OMS (Spiderfier) object
      this.oms = new OverlappingMarkerSpiderfier(this.map, {keepSpiderfied: true});
      //Creating a MarkerClusterer object
      this.mc = new MarkerClusterer(this.map, {gridSize: 20, maxZoom: 20});
      //initial load of markers, default is all time
      this.loadAllMarkers("");
      //creating InfoWindow object and listener for clicking on pins
      iw = new google.maps.InfoWindow();
      this.oms.addListener('click', $.proxy(function(m) {
        iw.setContent(m.desc);
        iw.open(this.map, m);
      }, this));

      this.oms.addListener('spiderfy', $.proxy(function(markers) {
        for(var i = 0; i < markers.length; i ++) {
          //markers[i].fillColor("blue");//setIcon('http://maps.google.com/mapfiles/ms/icons/blue-dot.png'); //do this in the template file?
          //markers[i].setShadow(null);
        } 
        iw.close();
      }, this));

      this.oms.addListener('unspiderfy', $.proxy(function(markers) {
        for(var i = 0; i < markers.length; i ++) {
          //need to set it to be the default icon again
          //markers[i].setIcon('http://maps.google.com/mapfiles/ms/icons/blue-dot.png');
          //markers[i].setShadow(null);
        } 
        iw.close();
      }, this));

      /*google.maps.event.addListener(this.mc, 'clusterclick', function(cluster) {
        map.setCenter(cluster.getCenter());
      });*/

      vent.on('mapFilter', $.proxy(function (hours) {
        this.oms.unspiderfy();
        this.clearMarkers();
        this.loadAllMarkers(hours);
      }, this));
    },

    /*TODO
      1.triggering listener to infowindow to listen for click on album art to play it
      2.implement google markerclusterer library
      different colors on spiderfying
      cycle through colors for each different coordinate
    */

    loadAllMarkers: function (hours) {
      var timePeriod = (hours) ? "&pastHours=" + hours : "";
      $.get('/rate?limit=10' + timePeriod, $.proxy(function (data) {
        for (var position in data) {
          data[position].reverse().forEach($.proxy(function (d) {
            var split = position.split(",");
            var latLng = new google.maps.LatLng(split[0], split[1]);
            this.addNewMarker(latLng, d.title, d.artist, d.album, d.image, d.score, d.rdioId);
          }, this));
        }
      }, this));
    },

    addNewMarker: function (position, song, artist, album, image, score, key) {
      var marker = new google.maps.Marker({
        position: position,
        map: this.map,
        title: song,
        desc: template({
          image: image,
          song: song,
          album: album,
          artist: artist,
          score: score,
          key: key
        })
      });
      this.oms.addMarker(marker);
      //this.mc.addMarker(marker);
      markerArray.push(marker);
    },

    clearMarkers: function () {
      if (iw) {
        iw.close();
      }
      if (markerArray) {
        for (i=0; i < markerArray.length; i++) {
            markerArray[i].setMap(null);
        }
      markerArray.length = 0;
      }
    },

    iconWithColor: function () {
      return newImage;
        //color + '|000000|ffff00';
    },

    playSong: function () {
      vent.trigger('play-key', this.$('#iw-key').val());
    }

  }))();
});
