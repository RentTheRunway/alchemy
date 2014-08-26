define([
  'jquery'
  'underscore'
  'backbone'
  'libs/text!templates/experiments/_list_item.html',
  'views/experiments/show'
], ($, _, Backbone, experimentListItemTemplate, ExperimentShowView) ->
	ListItemView = Backbone.View.extend(
		tagName: 'tr'
		className: 'list-item'
		render: ->
			compiledTemplate = _.template experimentListItemTemplate, @model
			@$el.html(compiledTemplate)
			if @model.get("active")
				@$el.find(".label-success").show()
				@$el.find(".label-default").hide()
			else
				@$el.find(".label-default").show()
				@$el.find(".label-success").hide()

			@
	)
)
