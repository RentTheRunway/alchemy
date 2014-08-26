define([
	'underscore',
	'backbone',
	'models/treatment'
], (_, Backbone, TreatmentModel) ->
	TreatmentsCollection = Backbone.Collection.extend(
		model: TreatmentModel
	)
)