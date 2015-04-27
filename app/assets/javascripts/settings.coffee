root = exports ? this

$ ->
  sc_settings.employees ( $('#employees') )
  sc_settings.holidays ( $( '#holidays' ) )
  sc_settings.sprints( $( '#sprints' ) )

  $("editable-listbox").on('data-changed',sc_main.dataChangedHandler)

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
      success: sc_main.dataPosted
      error: sc_main.onError



root.sc_settings =
  employees : (comp) ->
    settingsJsRoutes.controllers.Settings.employees().ajax(sc_main.fillRowsAjaxCall(comp))
  holidays : (comp) ->
    settingsJsRoutes.controllers.Settings.holidays().ajax(sc_main.fillRowsAjaxCall(comp))
  sprints : (comp) ->
    settingsJsRoutes.controllers.Settings.sprints().ajax(sc_main.fillRowsAjaxCall(comp))

root.employee_editbox =
  convert : (txt) ->
    JSON.parse('{"label":"'+txt+'","name":"'+txt+'"}')

root.holiday_editbox =
  validate : (txt) ->
    pattern = /^[\w\s]+[,;: \t]+(\d{4}-)*\d{1,2}-\d{1,2}\s*$/
    datePattern = /(\d{4}-)*\d{1,2}-\d{1,2}\s*$/g
    if(pattern.exec(txt))
      dates = txt.match(datePattern)
      dateStr = dates[dates.length-1].trim()
      if(dateStr.length < 6)
        # convert to date to verify if data is correct, 2012 is leap year
        dateStr = '2012-'+dateStr
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
    pattern = /^[\w\d\s.]+[,;: \t]+\d{4}-\d{1,2}-\d{1,2}::\d{4}-\d{1,2}-\d{1,2}\s*$/
    datePattern = /\d{4}-\d{1,2}-\d{1,2}\s*/g
    if(pattern.exec(txt))
      dates = txt.match(datePattern)
      dateStr = dates[dates.length-2]
      dateFrom = new Date(dateStr)
      if(isNaN(dateFrom.getTime()))
        "Invalid date - start of sprint"
      else
        dateStr = dates[dates.length-1]
        dateTo = new Date(dateStr.trim())
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
    datesPattern = /\d{4}-\d{1,2}-\d{1,2}::\d{4}-\d{1,2}-\d{1,2}\s*$/
    datesPart = datesPattern.exec(txt)
    namePart = txt.substring(0, txt.length-datesPart[0].length).trim()
    dates = datesPart[0].split("::")
    JSON.parse('{"label":"'+txt+'","name":"'+namePart+'","from":"'+dates[0]+'","to":"'+dates[1].trim()+'"}')

