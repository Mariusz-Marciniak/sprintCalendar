root = exports ? this

component = undefined
root.users = (comp) ->
    component = comp
    settingsJsRoutes.controllers.Settings.users().ajax({success: onSuccess, error: onError})

onSuccess = (data) ->
    value = data ? []
    component.setAttribute('rows',value)

onError = (jqHXR, error) ->
    console.warn("error:"+error)