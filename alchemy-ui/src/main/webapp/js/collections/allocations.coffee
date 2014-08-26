define([
	'underscore',
	'backbone',
	'models/allocation'
], (_, Backbone, AllocationModel) ->
	AllocationsCollection = Backbone.Collection.extend(
		model: AllocationModel
	)
)