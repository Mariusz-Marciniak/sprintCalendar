root = exports ? this

$ ->
  sc_settings.employees ( $('#employees') )
  sc_settings.holidays ( $( '#holidays' ) )
  sc_settings.sprints( $( '#sprints' ) )

  dataChangedHandler = () -> sc_main.dataChanged = true
  $('#employees').on('dataChanged',dataChangedHandler)
  $('#holidays').on('dataChanged',dataChangedHandler)
  $('#sprints').on('dataChanged',dataChangedHandler)

  $("#saveBtn").click ->
    $("#success-bar").hide()
    $("#error-bar").hide()
    employeesSet = $("#employees").prop("rows")
    holidaySet = $("#holidays").prop("rows")
    sprintSet = $("#sprints").prop("rows")
    $.ajax
      url: settingsJsRoutes.controllers.Settings.saveSettings().url
      contentType: "application/json"
      method: "POST"
      successMessage: "Data was successfully saved"
      errorMessage: "Save operation failed"
      data: '{"employees":'+JSON.stringify(employeesSet)+', "holidays":'+JSON.stringify(holidaySet)+', "sprints":'+JSON.stringify(sprintSet)+'}'
      success: dataPosted
      error: onError



root.sc_settings =
  employees : (comp) ->
    settingsJsRoutes.controllers.Settings.employees().ajax(fillRowsAjaxCall(comp))
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

dataPosted = (data) ->
  sc_main.showMessageBar($("#success-bar"),this.successMessage)
  sc_main.dataChanged = false

onError = (jqHXR, error, status) -> sc_main.showMessageBar($("#error-bar"),this.errorMessage ? jqHXR.responseText)

root.employee_editbox =
  convert : (txt) ->
    JSON.parse('{"label":"'+txt+'","name":"'+txt+'"}')

root.holiday_editbox =
  validate : (txt) ->
    pattern = /^[\w\s]+[,;: \t]+(\d{4}-)*\d{1,2}-\d{1,2}\s*$/
    datePattern = /(\d{4}-)*\d{1,2}-\d{1,2}\s*$/g
    if(pattern.exec(txt))
      dates = txt.match(datePattern)
      dateStr = dates[dates.length-1]
      if(dateStr.length < 6)
        dayAndMonth = dateStr.split("-")
        # convert to date to verify if data is correct, 2012 is leap year
        dateStr = '2012-'+dayAndMonth[1]+'-'+dayAndMonth[0]
      date = new Date(dateStr)
      if(isNaN(date.getTime()))
        "Invalid date"
      else
        null
    else
      "Please enter holiday name followed by date yyyy-mm-dd or mm-dd (for each year)"
  convert : (txt) ->
    pattern = /^[\w\s]+\w+/
    datePattern = /(\d{4}-)*\d{1,2}-\d{1,2}\s*$/g
    dates = txt.match(datePattern)
    dateStr = dates[dates.length-1]
    name = pattern.exec(txt.substring(0,txt.length - dateStr.length))
    JSON.parse('{"label":"'+txt+'","name":"'+name+'","date":"'+dateStr+'"}')

root.sprint_editbox =
  validate : (txt) ->
    pattern = /^[\w\s]+[,;: \t]+\d{4}-\d{1,2}-\d{1,2}::\d{4}-\d{1,2}-\d{1,2}\s*$/
    datePattern = /\d{4}-\d{1,2}-\d{1,2}\s*/g
    if(pattern.exec(txt))
      dates = txt.match(datePattern)
      dateStr = dates[dates.length-2]
      dateFrom = new Date(dateStr)
      if(isNaN(dateFrom.getTime()))
        "Invalid date - start of sprint"
      else
        dateStr = dates[dates.length-1]
        dateTo = new Date(dateStr)
        if(isNaN(dateTo.getTime()))
          "Invalid date - end of sprint"
        else
          if(dateFrom >= dateTo)
            "End of sprint should occur after beginning"
          else
            null
    else
      "Please enter sprint name followed by period yyyy-mm-dd::yyyy-mm-dd"
  convert : (txt) ->
    namePattern = /^[\w\s]+\w+/
    datesPattern = /\d{4}-\d{1,2}-\d{1,2}::\d{4}-\d{1,2}-\d{1,2}\s*$/
    datePattern = /\d{4}-\d{1,2}-\d{1,2}\s*/g
    datesPart = datesPattern.exec(txt)
    namePart = namePattern.exec(txt.substring(0,txt.length-datesPart[0].length))
    dates = datesPart[0].split("::")
    JSON.parse('{"label":"'+txt+'","name":"'+namePart+'","from":"'+dates[0]+'","to":"'+dates[1].trim()+'"}')

