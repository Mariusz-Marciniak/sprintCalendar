root = exports ? this

$ ->
  dashboardJsRoutes.controllers.Dashboard.timelineData().ajax(sc_main.fillRowsAjaxCall($('#dashboard-timeline')))

  $("#vacation-info").hide()

  $("#dashboard-timeline").on("clicked", (e) ->
    if(sc_dashboard.hintTimeout != undefined)
      clearTimeout(sc_dashboard.hintTimeout)
    vi = $("#vacation-info")
    details = e.originalEvent.detail
    vi.css("top",details.clientY + $(document).scrollTop())
    vi.css("left",details.clientX + $(document).scrollLeft())
    vi.html(details.entry.employee+"&nbsp;&nbsp;&nbsp;"+details.entry.label)
    vi.show(0, ()->
      sc_dashboard.hintTimeout = setTimeout ( =>
        $(@).hide()
      ), 5000
    )
  )

  $("#predef-ranges").on("core-select", (e) ->
    alert(e.originalEvent.detail)
    range = e.originalEvent.detail.item.innerText.split("::")
    alert(e.originalEvent)
    if(range.length > 1)
      to = range[range.length-1].trim()
      from = range[range.length-2]
      alert(from)
      alert(to)
      if(from.length > 10)
        from = from.substring(from.length-10)
        $("#fromDate").attr("value",from)
        $("#toDate").attr("value",to)
        sc_dashboard.refreshTimeline()
  )

root.sc_dashboard =
  hintTimeout: undefined,
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
      dashboardJsRoutes.controllers.Dashboard.saveDefaults(strFrom, strTo).ajax().done(() -> location.reload())
      true
