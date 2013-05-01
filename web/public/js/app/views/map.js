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
  //first set to 10s
  var timer = 10000;
  var timerMultiplier = 1;
  var thread = null;
  var prevData = null;

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
      this.mc = new MarkerClusterer(this.map, [], {gridSize: 80, maxZoom: 6});

      //creating InfoWindow object and listener for clicking on pins
      iw = new google.maps.InfoWindow();
      this.oms.addListener('click', $.proxy(function(m) {
        iw.setContent(m.desc);
        iw.open(this.map, m);
      }, this));

      //creating the MarkerImage for the default red marker and its shadow
      this.redIcon = {
        url: '/img/sprite.png',
        //size of marker image
        size: new google.maps.Size(20,34),
        //position of image in sprite
        origin: new google.maps.Point(100,400),
        //where to anchor the image to the map (ie the bottom tip of the marker in this case)
        anchor: new google.maps.Point(10,34)
      };

      this.redIconShadow = {
        url: '/img/sprite.png',
        // The shadow image is larger in the horizontal dimension
        // while the position and offset are the same as for the main image.
        size: new google.maps.Size(37, 34),
        origin: new google.maps.Point(120,434),
        anchor: new google.maps.Point(10, 34)
      };

      //creating the MarkerImage for the blue marker and its shadow
      this.blueIcon = {
        url: '/img/sprite.png',
        size: new google.maps.Size(20,34),
        origin: new google.maps.Point(100,434),
        anchor: new google.maps.Point(10,34)
      };

      this.blueIconShadow = {
        url: '/img/sprite.png',
        size: new google.maps.Size(37, 34),
        origin: new google.maps.Point(120,434),
        anchor: new google.maps.Point(10, 34)
      };

      //listener for spiderify to set the markers to blue
      this.oms.addListener('spiderfy', $.proxy(function (markers) {
        this.map.panTo(markers[0].getPosition());
        for (var i = 0; i < markers.length; i ++) {
          markers[i].setIcon(this.blueIcon);
          markers[i].setShadow(this.blueIconShadow);
        }
        iw.close();
      }, this));

      //listener for unspiderfy to reset markers to red
      this.oms.addListener('unspiderfy', $.proxy(function (markers) {
        for (var i = 0; i < markers.length; i ++) {
          markers[i].setIcon(this.redIcon);
          markers[i].setShadow(this.redIconShadow);
        }
        iw.close();
      }, this));

      //default is all time: ""
      this.hours = "";
      //listening for mapFilter event which clears and reloads markers based on # hrs passed
      vent.on('mapFilter', $.proxy(function (hours) {
        //since the user triggered a filter change, clear any timer
        clearTimeout(thread);
        this.clearMarkers();
        //saves the hours filter
        this.hours = hours;
        //clears previous data since this is a new hours filter
        prevData = null;
        this.loadAllMarkers(this.hours);
      }, this));

      //initial load of markers, default is all time
      this.loadAllMarkers(this.hours);
    },

    //loads all markers based on time period from server
    loadAllMarkers: function (hours) {
      if (this.$req) this.$req.abort();
      var timePeriod = (hours) ? "&pastHours=" + hours : "";
      this.$req = $.get('/rate?limit=10' + timePeriod, $.proxy(function (data) {
        //checks to see if the data has changed. if so, then update
        if (_.isEqual(prevData,data)) {
          //same, so increase the multiplier and starts the timer
          timerMultiplier++;
          this.startTimer();
          vent.trigger('pinsLoaded');
          return;
        }
        //new data, so clear and reload
        this.clearMarkers();
        timerMultiplier = 1;
        prevData = data;
        for (var position in data) {
          var rank = data[position].length;
          data[position].reverse().forEach($.proxy(function (d) {
            var split = position.split(",");
            var latLng = new google.maps.LatLng(split[0], split[1]);
            this.addNewMarker(latLng, d.title, d.artist, d.album, d.image, d.score, d.rdioId, rank);
            rank--;
          }, this));
          data[position].reverse();
        }
        this.startTimer();
        vent.trigger('pinsLoaded');
      }, this));
    },

    //creates a single new marker and adds to Spiderfier, MarkerClusterer, and local arrays for markers
    addNewMarker: function (position, song, artist, album, image, score, key, rank) {
      var marker = new google.maps.Marker({
        position: position,
        map: this.map,
        title: "#" + rank + ". " + song,
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
      this.mc.addMarker(marker);
      markerArray.push(marker);
    },

    //clears all markers from Spiderfier, MarkerCluster, and local arrays for markers
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
      this.oms.unspiderfy();
      this.oms.clearMarkers();
      this.mc.clearMarkers();
    },

    //triggers the play-key event to begin playing the song in the marker InfoWindow
    playSong: function () {
      vent.trigger('play-key', this.$('#iw-key').val());
    },

    //starts up the timer for refreshing data
    startTimer: function () {
      //clears the timeout thread first
      clearTimeout(thread);
      //initializes the timeout thread based on the multiplier
      thread = setTimeout($.proxy(function () {
        this.loadAllMarkers(this.hours);
      }, this), timer*Math.pow(2, timerMultiplier));
    }
  }))();
});
