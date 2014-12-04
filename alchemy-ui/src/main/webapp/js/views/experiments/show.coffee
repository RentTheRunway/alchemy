define([
  'jquery'
  'underscore'
  'backbone'
  'flot'
  'flotpie'
  'bootstrap'
  'libs/text!templates/experiments/_show.html'
  'views/treatments/new'
  'models/experiment_update'
], ($, _, Backbone, flot, flotpie, bootstrap, showTemplate, TreatmentFormView, ExperimentUpdate) ->
	ShowView = Backbone.View.extend(
		events:
			"click .add-more-treatment": "newTreatmentForm"
			"click .activate-experiment": "activateExperiment"
			"click .deactivate-experiment": "deactivateExperiment"

		initialize: ->
			@listenTo(@model, "sync", @render)
			@model.fetch()
			
		render: ->
			treatment_allocation_map = _.reduce(
			  @model.get("treatments")
			  (arr, t) => arr.push { name: t.name, description: t.description }; arr
			  []
			)
			treatment_allocation_map = _.each(
				treatment_allocation_map
				(t) => t.size = (found = _.find(@model.get("allocations"), (a) -> a.treatment is t.name)) && found.size or 0
			)
			
			compiledTemplate = _.template showTemplate, { model: @model, treatment_allocation_map: treatment_allocation_map }
			content = @$el.html compiledTemplate
			$("#page-content").html(content)

			$(".js-page-title").text("Experiment #{@model.get('name')}")
			$(".js-page-subtitle").text(@model.get('description'))
			
			treatments = @model.get "treatments"
			allocations = @model.get "allocations"

			if !!allocations and allocations.length > 0
				totalAllocated = _.reduce(
					allocations
					(sum, a) -> sum + a.size 
					0)
				
				data = _.map(
					allocations,
					(a) ->
						label: _.find(
								treatments,
								(t) -> a.treatment == t.name
							).description,
						data: a.size
				)
				
				data.push(label: "unallocated", data: 100 - totalAllocated, color: "#979797")

				$.plot(
					$(".alloc-pie-chart")
					data
					series: 
						pie: 
							show: true
					grid:
						hoverable: true
						clickable: true
					legend:
						show: false
				)

			@delegateEvents()	# http://stackoverflow.com/questions/9379854/backbone-rebinding-events-on-a-view

		newTreatmentForm: (e) ->
			new TreatmentFormView(model: @model)

		activateExperiment: (e) ->
			experimentUpdate = new ExperimentUpdate(
				active: true
			)
			experimentUpdate.sync("create", experimentUpdate, {
				url: "/api/experiments/#{@model.get('name')}"
				complete: (jqXHR, textStatus) =>
					if jqXHR.status == 204
						@model.fetch()
					else
						alert('Failed')
			});

		deactivateExperiment: (e) ->
			experimentUpdate = new ExperimentUpdate(
				active: false
			)
			experimentUpdate.sync("create", experimentUpdate, {
				url: "/api/experiments/#{@model.get('name')}"
				complete: (jqXHR, textStatus) =>
					if jqXHR.status == 204
						@model.fetch()
					else
						alert('Failed')
			});
	)
)


	