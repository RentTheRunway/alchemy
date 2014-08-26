define([
	'underscore',
	'backbone'
], (_, Backbone) ->
	ExperimentModel = Backbone.Model.extend(
		idAttribute: "name"
		urlRoot: "/api/experiments"
	)
)