root = exports ? this


$("#saveBtn").click ->
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
  '{"xxx":"yyy"}'
