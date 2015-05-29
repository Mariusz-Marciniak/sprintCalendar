root = exports ? this

$ ->
  dashboardJsRoutes.controllers.Dashboard.timelineData().ajax(sc_main.fillRowsAjaxCall($( '#dashboard-timeline' )))

  $("#dashboard-timeline").on("clicked", (e) ->
    alert(JSON.stringify(e.originalEvent.detail.entry))
  )

root.sc_dashboard =
  refreshTimeline: () ->
    strFrom = $('#fromDate').prop('value')
    strTo = $('#toDate').prop('value')
    dateFrom = new Date(strFrom)
    dateTo = new Date(strTo)

    if(isNaN(dateFrom.getTime()))
      sc_main.showMessageBar($("#error-bar"), "Invalid date - start of timeline range")
      false
    else if(isNaN(dateTo.getTime()))
      sc_main.showMessageBar($("#error-bar"), "Invalid date - end of timeline range")
      false
    else if(dateFrom >= dateTo)
      sc_main.showMessageBar($("#error-bar"), "End of timeline should occur after beginning")
      false
     else
      dashboardJsRoutes.controllers.Dashboard.saveDefaults(strFrom, strTo).ajax().always(location.reload())
      true
