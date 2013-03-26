/*global define*/
define(['backbone'], function (bb) {
  // Create constructor function for songs
  var Song = bb.Model.extend({urlRoot: '/song'});

  // Create collection for songs
  var songs = new (bb.Collection.extend({
    model: Song,
    url: '/song'
  }))();

  // Get song from server (locally cached)
  Song.get = function (id, done) {
    var song = songs.get(id);
    if (song) {
      done(song);
    } else {
      song = new Song({id: id});
      songs.add(song);
      song.once('change', function () {
        done(song);
      });
      song.fetch();
    }
  };

  // Return constructor function
  return Song;
});
