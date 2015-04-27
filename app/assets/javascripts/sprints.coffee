root = exports ? this

$ ->
  sprintsJsRoutes.controllers.Sprints.sprints().ajax(sc_main.fillRowsAjaxCall($( '#sprints-list' )))


