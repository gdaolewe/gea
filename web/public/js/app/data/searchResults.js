/*global define*/
define(['backbone'], function (bb) {
  /* This is the Collection aspect of the Model-View-Collection design pattern.
   * When results come back (in JSON format), they are pumped into this collection.
   * This generates a generic Backbone Model, which is the
   * Model aspect of the Model-View-Collection design pattern.
   * When that is complete, it fires an event to say that it has changed.
  */
  // Return new collection which holds search results
  return new (bb.Collection.extend())();
});
