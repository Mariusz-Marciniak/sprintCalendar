root = exports ? this


$("#saveSprintDataBtn").click ->
  saveSprintData(false)

$("#confirmBtn").click ->
  if(this.getAttribute("active")==null)
    saveSprintData(true)

saveSprintData = (confirmed) ->
  $("#success-bar").hide()
  $("#error-bar").hide()
  sprint = $("#sprintNameLabel").html()
  $.ajax
    url: sprintsJsRoutes.controllers.Sprints.saveSprintData(sprint).url
    contentType: "application/json"
    method: "POST"
    successMessage: "Data was successfully saved"
    errorMessage: "Save operation failed"
    data: prepareData(confirmed)
    success: confirmAndRefreshSprintData
    error: sc_main.onError

confirmAndRefreshSprintData = (data) ->
  sc_main.dataPosted
  sc_sprints.refreshSprint(data)

prepareData = (confirmed) ->
  sprintTxt = '{ "storyPoints":'+$("#storyPoints").prop("value")
  sprintTxt += ',"confirmed": '+confirmed
  sprintTxt += ',"workload": ['
  $.each($("paper-slider"),
    (index, component) ->
      if(index > 0)
        sprintTxt += ','
      sprintTxt += '{"employee":"'+sc_main.escape(null, component.getAttribute("id"))+'","availability":'+component.value
      if(confirmed)
        sprintTxt += ', "maxAvailability":'+component.max
      sprintTxt += '}'
  )
  sprintTxt += ']}'
  sprintTxt

root.sc_sprintPanel =
  calcStoryPoints: () ->
    possibleStoryPointsSelector = $("#possibleStoryPoints")
    if(possibleStoryPointsSelector.length)
      sum = 0
      $("paper-slider").each(( index, component ) ->
        sum += component.dataset.storyPoints * component.value
      )
      possibleStoryPointsSelector.html(Math.round(sum))

$ ->
  $("paper-slider").on("change", sc_sprintPanel.calcStoryPoints)
