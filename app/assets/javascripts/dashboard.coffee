root = exports ? this

$ ->
  dashboardJsRoutes.controllers.Dashboard.timelineData().ajax(sc_main.fillRowsAjaxCall($( '#dashboard-timeline' )))
