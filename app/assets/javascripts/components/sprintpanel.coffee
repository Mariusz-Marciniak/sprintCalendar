root = exports ? this


$("#saveSprintDataBtn").click ->
  $("#success-bar").hide()
  $("#error-bar").hide()
  $.ajax
    url: sprintsJsRoutes.controllers.Sprints.saveSprintData($("#sprints-list").prop("selected")).url
    contentType: "application/json"
    method: "POST"
    successMessage: "Data was successfully saved"
    errorMessage: "Save operation failed"
    data: prepareData()
    success: sc_main.dataPosted
    error: sc_main.onError

prepareData = () ->
  sprintTxt = '{ "storyPoints":'+$("#storyPoints").prop("value")
  sprintTxt += ',"workload": ['
  $.each($("paper-slider"),
    (index, component) ->
      if(index > 0)
        sprintTxt += ','
      sprintTxt += '{"employee":"'+sc_main.escape(null, component.getAttribute("id"))+'","availability":'+component.value+'}'
  )
  sprintTxt += ']}'
  sprintTxt
