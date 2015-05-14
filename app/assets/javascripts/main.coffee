root = exports ? this

root.sc_main =
  dataChanged: false

  showChangeContextWarning: () ->
    if(this.dataChanged)
      this.showMessageBar($("#error-bar"),"You haven't saved your changes. If you want to discard them please choose new tab one more time")
      this.dataChanged = false
      false

  showMessageBar : (bar, message) ->
    bar.text(message)
    bar.slideDown(300, ->
      setTimeout ( =>
        $(@).slideUp()
      ), 5000
    )

  dataChangedHandler : () -> sc_main.dataChanged = true

  executeOnSuccessAjaxCall : (comp, onSuccess) -> {
    receiver: comp
    success: onSuccess
    error: sc_main.onError
  }

  fillRowsAjaxCall : (comp) -> sc_main.executeOnSuccessAjaxCall(
      comp,
      (data) -> $(this.receiver).attr('rows',JSON.stringify(data))
  )

  postAjaxCall : () -> {
    success: sc_main.dataPosted
    error: sc_main.onError
  }

  dataPosted : (data) ->
    sc_main.showMessageBar($("#success-bar"),this.successMessage)
    sc_main.dataChanged = false

  onError : (jqHXR, error, status) -> sc_main.showMessageBar($("#error-bar"),this.errorMessage ? jqHXR.responseText)

  escape : (key, val) ->
    if (typeof(val)=="string")
      val.replace(/[\"]/g, '\\"')
      .replace(/[\\]/g, '\\\\')
      .replace(/[\/]/g, '\\/')
      .replace(/[\b]/g, '\\b')
      .replace(/[\f]/g, '\\f')
      .replace(/[\n]/g, '\\n')
      .replace(/[\r]/g, '\\r')
      .replace(/[\t]/g, '\\t')
    else
      val
