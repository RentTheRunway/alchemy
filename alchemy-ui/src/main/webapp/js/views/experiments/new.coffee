define([
  'jquery'
  'underscore'
  'backbone'
  'libs/text!templates/experiments/_new.html'
  'models/experiment'
  'views/experiments/show'
], ($, _, Backbone, newFormTemplate, Experiment, ShowView) ->
	NewFormView = Backbone.View.extend(
		tagName: 'div'

		className: 'new-form container'

		template: _.template newFormTemplate

		events:
			'submit form': 'submitNewExperiment'

		render: ->
			$('.js-page-title').text('Create a New Experiment')
			$('.js-page-subtitle').text('Please fill all required* fields')
			@$el.html @template()
			@

		submitNewExperiment: (e) ->
			e.preventDefault();
			$form = $(e.currentTarget)
			formData = $form.serializeArray()
			name = _.find(formData, (element) -> element.name is "experiment-name" ).value
			description = _.find(formData, (element) -> element.name is "experiment-description" ).value
			newExperiment = new Experiment(
				name: name
				description: description
			)

			@model = newExperiment

			newExperiment.sync "update", newExperiment, {
				url: "/api/experiments"
				complete: (jqXHR, textStatus) =>
					if jqXHR.status == 201
						window.location.href = "#experiments/#{@model.get('name')}"
					else
						# handle error
			}
	)
)
