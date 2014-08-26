define([
  'jquery'
  'underscore'
  'backbone'
  'views/experiments/show'
  'libs/text!templates/treatments/_new.html'
  'collections/treatments'
  'collections/allocations'
  'models/treatment'
  'models/allocation'
  'models/experiment_update'
], ($, _, Backbone, ShowExperimentView, newTreatmentFormTemplate, Treatments, Allocations, Treatment, Allocation, ExperimentUpdate) ->
	NewTreatmentFormView = Backbone.View.extend(
		tagName: 'div'

		className: 'treatment-new modal-content'

		template: _.template newTreatmentFormTemplate

		events:
			'submit form': 'submitNewTreatment'
		
		initialize: ->
			@render()

		render: ->
			@$el.html @template()
			$modalContent = $('.modal').find('.modal-dialog')
			$modalContent.html(@$el)
			$('.modal').modal()
			@

		submitNewTreatment: (e) ->
			e.preventDefault();
			$form = $(e.currentTarget)
			formData = $form.serializeArray()
			
			name = _.find(formData, (element) -> element.name is "treatment-name" ).value
			description = _.find(formData, (element) -> element.name is "treatment-description" ).value
			allocationSize = _.find(formData, (element) -> element.name is "treatment-allocation" ).value

			newTreatment = new Treatment(
				name: name
				description: description
			)
			
			treatments = new Treatments(@model.get("treatments"))
			treatments.add(newTreatment)			

			newAllocation = new Allocation(
				treatment: name
				size: allocationSize
			)
			
			allocations = new Allocations(@model.get("allocations"))
			allocations.add(newAllocation)

			# alchemy needs to change to allow for this
			_.each allocations.models, (a) -> a.unset("offset")

			experimentUpdate = new ExperimentUpdate(
				treatments: treatments.models
				allocations: allocations.models
			)

			# this can have concurrency issues!
			experimentUpdate.sync("create", experimentUpdate, {
				url: "/api/experiments/#{@model.get('name')}"
				complete: (jqXHR, textStatus) =>
					if jqXHR.status == 204
						$('.modal').modal('hide')
						@model.fetch()
					else
						alert('S')
						# handle error				
			})
	)
)
