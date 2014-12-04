define([
	'jquery'
	'underscore'
	'backbone'
	'views/experiments/new'
	'views/experiments/list'
	'views/experiments/show'
	'models/experiment'
], ($, _, Backbone, NewFormView, ListView, ShowView, Experiment) -> 
	AppRouter = Backbone.Router.extend(
		routes:
			'new': 'newForm',
			'experiments': 'experiments'
			'experiments/:name': 'experiment'

		newForm: -> 
			newFormView = new NewFormView().render().$el
			$('#page-content').html newFormView
			
		experiments: ->
			new ListView()

		experiment: (name) ->
			model = new Experiment(name: name)
			new ShowView(model: model)
	)

	initialize: ->
		router = new AppRouter
		Backbone.history.start()
)