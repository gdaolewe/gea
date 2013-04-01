/*global define*/
define([
  'backbone',
  'async!//maps.googleapis.com/maps/api/js?key=AIzaSyB6n1ohXSe-LZSKYD730M9ZWBPI5Z8nTJ4&sensor=false',
  'util/jqr!'
], function (bb) {
  // Maps API available as the variable `google`
  return new (bb.View.extend({
    el: '#map',
    initialize: function () {
      this.mapOptions = {
        center: new google.maps.LatLng(42.375200, -72.521200),
        zoom: 10,
        mapTypeId: google.maps.MapTypeId.ROADMAP
      };
      this.map = new google.maps.Map(this.el, this.mapOptions);
      this.resize();
    }
  }))();
});
