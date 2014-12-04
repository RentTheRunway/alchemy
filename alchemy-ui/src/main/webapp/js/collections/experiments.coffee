define([
	'underscore',
	'backbone',
	'models/experiment'
], (_, Backbone, ExperimentModel) ->
	ExperimentCollection = Backbone.Collection.extend(
		model: ExperimentModel
		url: "/api/experiments"
	)
)