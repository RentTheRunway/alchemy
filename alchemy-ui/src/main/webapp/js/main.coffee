require.config
  paths:
    jquery: '//cdnjs.cloudflare.com/ajax/libs/jquery/2.1.1/jquery.min'
    underscore: '//cdnjs.cloudflare.com/ajax/libs/underscore.js/1.6.0/underscore-min'
    backbone: '//cdnjs.cloudflare.com/ajax/libs/backbone.js/1.1.2/backbone-min'
    bootstrap: '//maxcdn.bootstrapcdn.com/bootstrap/3.2.0/js/bootstrap.min'
    flot: '//cdnjs.cloudflare.com/ajax/libs/flot/0.8.2/jquery.flot.min'
    flotpie: '/js/libs/jquery.flot.pie.min'

  shim:
    'bootstrap':
      deps: ['jquery']    
    'flot':
      deps: ['jquery']
      exports: '$.plot'
    'flotpie':
      deps: ['flot']


require(['application'], (Application) ->
  Application.initialize()
)