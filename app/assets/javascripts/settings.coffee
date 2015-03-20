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
      contentType: "application/json"
      method: "POST"
      successMessage: "Data was successfully saved"
      errorMessage: "Save operation failed"
      data: '{"users":'+JSON.stringify(userSet)+', "holidays":'+JSON.stringify(holidaySet)+', "sprints":'+JSON.stringify(sprintSet)+'}'
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

root.holiday_editbox =
  validate : (txt) ->
    pattern = /^[\w\s]+[,;: \t]+(\d{4}-)*\d{1,2}-\d{1,2}\s*$/g
    datePattern = /(\d{4}-)*\d{1,2}-\d{1,2}\s*$/g
    if(pattern.exec(txt))
      dates = txt.match(datePattern)
      dateStr = dates[dates.length-1]
      if(dateStr.length < 6)
        dateStr = '2012-'+dateStr
      date = new Date(dateStr)
      if(isNaN(date.getTime()))
        "Invalid date"
      else
        null
    else
      "Please enter holiday name followed by date yyyy-mm-dd or mm-dd (for each year)"
