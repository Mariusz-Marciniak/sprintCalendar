root = exports ? this

$ ->
  sc_settings.users ( $('#users') )
  sc_settings.holidays ( $( '#holidays' ) )
  sc_settings.sprints( $( '#sprints' ))
  $("#saveBtn").click ->
    $("#success-bar").hide()
    $("#error-bar").hide()
    userSet = $("#users").get(0).rows
    holidaySet = $("#holidays").get(0).rows
    sprintSet = $("#sprints").get(0).rows
    $.ajax
      url: settingsJsRoutes.controllers.Settings.saveSettings().url
      dateType: "json"
      method: "POST"
      successMessage: "Data was successfully saved"
      errorMessage: "Save operation failed"
      data: {"users":userSet, "holidays":holidaySet, "sprints":sprintSet}
      success: dataPosted
      error: onError



root.sc_settings =
  users : (comp) ->
    settingsJsRoutes.controllers.Settings.users().ajax(fillRowsAjaxCall(comp))
  holidays : (comp) ->
    settingsJsRoutes.controllers.Settings.holidays().ajax(fillRowsAjaxCall(comp))
  sprints : (comp) ->
    settingsJsRoutes.controllers.Settings.sprints().ajax(fillRowsAjaxCall(comp))

fillRowsAjaxCall = (comp) -> {
  invoker: comp
  success: fillRows
  error: onError
}

fillRows = (data) ->
    $(this.invoker).attr('rows',JSON.stringify(data))

postAjaxCall = () -> {
  success: dataPosted
  error: onError
}

dataPosted = (data) -> showMessageBar($("#success-bar"),this.successMessage)

onError = (jqHXR, error, status) -> showMessageBar($("#error-bar"),this.errorMessage ? jqHXR.responseText)

showMessageBar = (bar, message) ->
  bar.text(message)
  bar.slideDown(300, ->
    setTimeout ( =>
      $(@).slideUp()
    ), 5000
  )