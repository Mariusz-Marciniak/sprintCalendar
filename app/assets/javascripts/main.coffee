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
  fillRowsAjaxCall : (comp) -> {
    invoker: comp
    success: sc_main.fillRows
    error: sc_main.onError
  }
  fillRows : (data) ->
    $(this.invoker).attr('rows',JSON.stringify(data))
  postAjaxCall : () -> {
    success: sc_main.dataPosted
    error: sc_main.onError
  }
  dataPosted : (data) ->
    sc_main.showMessageBar($("#success-bar"),this.successMessage)
    sc_main.dataChanged = false
  onError : (jqHXR, error, status) -> sc_main.showMessageBar($("#error-bar"),this.errorMessage ? jqHXR.responseText)
