root = exports ? this

$ ->
  $.each($("editable-listbox"),
    (index, component) -> sc_vacations.vacations( component, component.getAttribute("id"))
  )

  $("editable-listbox").on('data-changed',sc_main.dataChangedHandler)

  $("#saveBtn").click ->
    $("#success-bar").hide()
    $("#error-bar").hide()
    $.ajax
      url: vacationsJsRoutes.controllers.Vacations.saveVacations().url
      contentType: "application/json"
      method: "POST"
      successMessage: "Data was successfully saved"
      errorMessage: "Save operation failed"
      data: prepareData()
      success: sc_main.dataPosted
      error: sc_main.onError

prepareData = () ->
    vacationsTxt = '{'
    $.each($("editable-listbox"),
      (index, component) ->
        if(index > 0)
          vacationsTxt += ','
        vacationsTxt += '"'+sc_main.escape(null, component.getAttribute("id"))+'":'+JSON.stringify(component.rows,sc_main.escape)
    )
    vacationsTxt += '}'
    vacationsTxt


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
      if(moment(dateStr, "YYYY-MM-DD", true).isValid())
        dateFrom = new Date(dateStr)
        dateStr = dates[dates.length-1].trim()
        if(moment(dateStr, "YYYY-MM-DD", true).isValid())
          dateTo = new Date(dateStr)
          if(dateFrom > dateTo)
            "End of holiday should occur after beginning"
          else
            null
        else
          "Invalid date - end of holiday"
      else
        "Invalid date - start of holiday"
    else if(datePattern.exec(txt))
      singleDay = new Date(txt.trim())
      if(moment(txt, "YYYY-MM-DD", true).isValid())
        null
      else
        "Invalid date"
    else
      "Please enter holiday period yyyy-mm-dd::yyyy-mm-dd"

  convert : (txt) ->
    datesPattern = /\d{4}-\d{1,2}-\d{1,2}::\d{4}-\d{1,2}-\d{1,2}\s*$/
    datesPart = datesPattern.exec(txt)
    if(datesPart)
      dates = datesPart[0].split("::")
      JSON.parse('{"label":"'+txt+'","accepted":false,"from":"'+dates[0]+'","to":"'+dates[1].trim()+'"}')
    else
      datePattern = /\d{4}-\d{1,2}-\d{1,2}/
      date = datePattern.exec(txt)
      label = date+"::"+date
      JSON.parse('{"label":"'+label+'","accepted":false,"from":"'+date+'","to":"'+date+'"}')

