define([
  'jquery'
  'underscore'
  'backbone'
  'collections/experiments'
  'views/experiments/list_item'
  'libs/text!templates/experiments/_list.html'
], ($, _, Backbone, Experiments, ListItemView, experimentsListTemplate) ->
	ListView = Backbone.View.extend(
		initialize: ->
			@collection = new Experiments()
			@listenTo(@collection, 'sync', @render)
			@collection.fetch()

		render: ->
			$('.js-page-title').text('Welcome to Alchemy')
			$('.js-page-subtitle').text('All Experiments')	
			$('#page-content').html experimentsListTemplate
			experiments = _.sortBy @collection.models, (e) -> -e.get('created')
			_.each(experiments, (model) ->
				$('#experiments-list').append(new ListItemView( model: model ).render().$el)
			)
	)
)
