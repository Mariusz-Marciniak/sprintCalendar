root = exports ? this

$ ->
  sprintsJsRoutes.controllers.Sprints.sprints().ajax(sc_main.fillRowsAjaxCall($( '#sprints-list' )))

  $("#sprints-list").on "core-select", (e) ->
    $.ajax
      url: sprintsJsRoutes.controllers.Sprints.sprintData(e.target.selected).url
      method: "GET"
      errorMessage: "Couldn't retrieve sprint data"
      success: sc_sprints.refreshSprint
      error: sc_main.onError

root.sc_sprints =
  refreshSprint : (data) ->
    $("#sprint-panel").html(data)
    setTimeout ( =>
      sc_sprintPanel.calcStoryPoints()
    ), 500


