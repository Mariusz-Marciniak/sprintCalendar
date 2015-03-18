root = exports ? this

root.sc_settings =
  users : (comp) ->
    settingsJsRoutes.controllers.Settings.users().ajax(ajaxCall(comp))
  holidays : (comp) ->
    settingsJsRoutes.controllers.Settings.holidays().ajax(ajaxCall(comp))
  sprints : (comp) ->
    settingsJsRoutes.controllers.Settings.sprints().ajax(ajaxCall(comp))

ajaxCall = (comp) -> {
  invoker: comp,
  success: onSuccess,
  error: onError
}

onSuccess = (data) ->
    $(this.invoker).attr('rows',JSON.stringify(data))

onError = (jqHXR, error) ->
    console.warn("error:"+error)