// Generated by CoffeeScript 1.7.1
(function() {
  define(['underscore', 'backbone', 'models/experiment'], function(_, Backbone, ExperimentModel) {
    var ExperimentCollection;
    return ExperimentCollection = Backbone.Collection.extend({
      model: ExperimentModel,
      url: "/api/experiments"
    });
  });

}).call(this);