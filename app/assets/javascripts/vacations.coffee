root = exports ? this

$ ->
  $.each($("editable-listbox"),
    (index, value) -> sc_vacations.vacations( value, value.getAttribute("id"))
  )
  $("#saveBtn").click ->
    $.each($("editable-listbox"), (index, value) -> alert(value.getAttribute("id")))

  $("editable-listbox").on('dataChanged',sc_main.dataChangedHandler)

root.sc_vacations =
  vacations : (comp,identifier) ->
    vacationsJsRoutes.controllers.Vacations.vacations(identifier).ajax(sc_main.fillRowsAjaxCall(comp))

root.vacation_editbox =
  validate : (txt) ->
    pattern = /^\d{4}-\d{1,2}-\d{1,2}::\d{4}-\d{1,2}-\d{1,2}\s*$/
    datePattern = /\d{4}-\d{1,2}-\d{1,2}\s*/g
    if(pattern.exec(txt))
      dates = txt.match(datePattern)
      dateStr = dates[dates.length-2]
      dateFrom = new Date(dateStr)
      if(isNaN(dateFrom.getTime()))
        "Invalid date - start of holiday"
      else
        dateStr = dates[dates.length-1]
        dateTo = new Date(dateStr)
        if(isNaN(dateTo.getTime()))
          "Invalid date - end of holiday"
        else
          if(dateFrom >= dateTo)
            "End of holiday should occur after beginning"
          else
            null
    else
      "Please enter holiday period yyyy-mm-dd::yyyy-mm-dd"
  convert : (txt) ->
    datesPattern = /\d{4}-\d{1,2}-\d{1,2}::\d{4}-\d{1,2}-\d{1,2}\s*$/
    datePattern = /\d{4}-\d{1,2}-\d{1,2}\s*/g
    datesPart = datesPattern.exec(txt)
    dates = datesPart[0].split("::")
    JSON.parse('{"label":"'+txt+'","from":"'+dates[0]+'","to":"'+dates[1].trim()+'"}')
